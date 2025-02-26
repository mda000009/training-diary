package com.isia.tfm.service.impl;

import com.isia.tfm.entity.*;
import com.isia.tfm.exception.CustomException;
import com.isia.tfm.model.CreateSessions201Response;
import com.isia.tfm.model.CreateSessionsRequest;
import com.isia.tfm.model.Session;
import com.isia.tfm.model.TrainingVariable;
import com.isia.tfm.repository.*;
import com.isia.tfm.service.SessionManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class SessionManagementServiceImpl implements SessionManagementService {
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SessionExerciseRepository sessionExerciseRepository;
    @Autowired
    private TrainingVariablesRepository trainingVariablesRepository;
    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public CreateSessions201Response createSessions(CreateSessionsRequest createSessionsRequest) {
        CreateSessions201Response createSessions201Response = new CreateSessions201Response();
        List<ExerciseEntity> exerciseEntityList = getExerciseToCreateList(createSessionsRequest);
        List<Session> sessionList = createSessionsRequest.getSessions();
        saveSessions(sessionList, exerciseEntityList);
        log.debug("Saved training sessions");
        try {
            saveTrainingVolume(sessionList);
            log.debug("Training volume for each exercise of each session saved");
        } catch (Exception e) {
            log.error("Training volume could not be calculated and saved");
        }
        try {
            sendTrainingSessionEmail(createSessionsRequest.getDestinationEmail(), sessionList);
            log.debug("An email successfully sent for each saved training session");
        } catch (Exception e) {
            log.error("The information email could not be sent");
        }
        createSessions201Response.setMessage("Sessions successfully created.");
        return createSessions201Response;
    }

    private List<ExerciseEntity> getExerciseToCreateList(CreateSessionsRequest createSessionsRequest) {
        List<Integer> exerciseToCreateList = createSessionsRequest.getSessions().stream()
                .flatMap(session -> session.getTrainingVariables().stream())
                .map(TrainingVariable::getExerciseId)
                .toList();
        Set<Integer> createdExerciseSet = new HashSet<>(exerciseRepository.findAllExerciseIds());
        String exerciseNotCreated = exerciseToCreateList.stream()
                .filter(exercise -> !createdExerciseSet.contains(exercise))
                .findFirst()
                .map(String::valueOf)
                .orElse(null);
        if (exerciseNotCreated != null) {
            throw new CustomException("404", "Not found", "The exercise with ID " + exerciseNotCreated + " is not created");
        } else {
            return exerciseRepository.findAllById(exerciseToCreateList);
        }
    }

    @Transactional
    private void saveSessions(List<Session> sessionList, List<ExerciseEntity> exerciseEntityList) {
        for (Session session : sessionList) {
            ApplicationUserEntity applicationUserEntity = applicationUserRepository.findById(session.getUsername())
                    .orElseThrow(() -> new CustomException("404", "Not found", "User with ID " + session.getUsername() + " not found"));
            SessionEntity sessionEntity = new SessionEntity(session.getSessionId(), session.getSessionName(), session.getSessionDate(), applicationUserEntity);
            boolean createdSession = sessionRepository.findById(sessionEntity.getSessionId()).isPresent();
            if (!createdSession) {
                sessionRepository.save(sessionEntity);
                List<TrainingVariable> trainingVariableList = session.getTrainingVariables();
                saveSessionExercises(exerciseEntityList, sessionEntity, trainingVariableList);
            }
        }
    }

    private void saveSessionExercises(List<ExerciseEntity> exerciseEntityList,
                                     SessionEntity sessionEntity,
                                     List<TrainingVariable> trainingVariableList) {
        for (ExerciseEntity exerciseEntity : exerciseEntityList) {
            SessionExerciseEntity sessionExerciseEntity = new SessionExerciseEntity(sessionEntity.getSessionId(),
                    exerciseEntity.getExerciseId(), null, sessionEntity, exerciseEntity);
            boolean createdSessionExercise = sessionExerciseRepository.findById(
                    new SessionExercisePK(sessionExerciseEntity.getSessionId(), sessionExerciseEntity.getExerciseId()))
                    .isPresent();
            if (!createdSessionExercise) {
                sessionExerciseRepository.save(sessionExerciseEntity);
                saveTrainingVariables(trainingVariableList, sessionExerciseEntity);
            }
        }
    }

    private void saveTrainingVariables(List<TrainingVariable> trainingVariableList,
                                       SessionExerciseEntity sessionExerciseEntity) {
        for (TrainingVariable trainingVariable : trainingVariableList) {
            TrainingVariablesEntity trainingVariablesEntity = new TrainingVariablesEntity(
                    trainingVariable.getSetNumber(), sessionExerciseEntity,
                    trainingVariable.getWeight(), trainingVariable.getRepetitions(), trainingVariable.getRir());
            trainingVariablesRepository.save(trainingVariablesEntity);
        }
    }

    @Transactional
    private void saveTrainingVolume(List<Session> sessionList) {
        for (Session session : sessionList) {
            List<SessionExerciseEntity> sessionExerciseEntityList = sessionExerciseRepository.findBySessionId(session.getSessionId());
            for (SessionExerciseEntity sessionExerciseEntity : sessionExerciseEntityList) {
                List<TrainingVariablesEntity> trainingVariablesEntityList =
                        trainingVariablesRepository.findBySessionExercise(sessionExerciseEntity);
                BigDecimal trainingVolume = new BigDecimal(0);
                for (TrainingVariablesEntity trainingVariablesEntity : trainingVariablesEntityList) {
                    trainingVolume = trainingVolume.add(trainingVariablesEntity.getWeight().
                            multiply(new BigDecimal(trainingVariablesEntity.getRepetitions())));
                }
                sessionExerciseEntity.setTrainingVolume(trainingVolume);
                sessionExerciseRepository.save(sessionExerciseEntity);
            }
        }
    }

    private void sendTrainingSessionEmail(String destinationEmail, List<Session> sessionList) {
        String user = sessionList.get(0).getUsername();
        for (Session session : sessionList) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(destinationEmail);
            message.setSubject("Training Diary App");
            message.setText("User " + user + " has registered a new training session on " + session.getSessionDate().toString());
            emailSender.send(message);
        }
    }
}