package com.isia.tfm.service.impl;

import com.isia.tfm.entity.*;
import com.isia.tfm.exception.CustomException;
import com.isia.tfm.model.*;
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
import java.math.RoundingMode;
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
        List<ExerciseEntity> exerciseEntityList = getExerciseToCreateList(createSessionsRequest);
        List<ReturnSession> returnSessionList = saveSessions(createSessionsRequest.getSessions(), exerciseEntityList);
        List<Session> filteredSessionList = filterSessionsCreated(createSessionsRequest.getSessions(), returnSessionList);
        log.debug("Saved training sessions");

        try {
            saveTrainingVolume(filteredSessionList);
            log.debug("Training volume for each exercise of each session saved");
        } catch (Exception e) {
            log.error("Training volume could not be calculated and saved");
        }

        try {
            sendTrainingSessionEmail(createSessionsRequest.getDestinationEmail(), filteredSessionList);
            log.debug("An email successfully sent for each saved training session");
        } catch (Exception e) {
            log.error("The information email could not be sent");
        }

        CreateSessions201Response createSessions201Response = new CreateSessions201Response();
        createSessions201Response.setSessions(returnSessionList);
        return createSessions201Response;
    }

    private List<ExerciseEntity> getExerciseToCreateList(CreateSessionsRequest createSessionsRequest) {
        List<Integer> exerciseToCreateList = createSessionsRequest.getSessions().stream()
                .flatMap(session -> session.getTrainingVariables().stream())
                .map(TrainingVariable::getExerciseId)
                .toList();
        Set<Integer> createdExerciseSet = new HashSet<>(exerciseRepository.findAllExerciseIds());
        Optional<String> exerciseNotCreated = exerciseToCreateList.stream()
                .filter(exercise -> !createdExerciseSet.contains(exercise))
                .findFirst()
                .map(String::valueOf);

        exerciseNotCreated.ifPresent(exerciseId -> {
            throw new CustomException("404", "Not found", "The exercise with ID " + exerciseId + " is not created");
        });
        return exerciseRepository.findAllById(exerciseToCreateList);
    }

    @Transactional
    private List<ReturnSession> saveSessions(List<Session> sessionList, List<ExerciseEntity> exerciseEntityList) {
        return sessionList.stream()
                .map(session -> {
                    ApplicationUserEntity applicationUserEntity = applicationUserRepository.findById(session.getUsername())
                            .orElseThrow(() ->
                                    new CustomException("404", "Not found", "User with username "
                                            + session.getUsername() + " not found"));
                    SessionEntity sessionEntity = new SessionEntity(session.getSessionId(), session.getSessionName(),
                            session.getSessionDate(), applicationUserEntity);
                    if (!sessionRepository.findById(sessionEntity.getSessionId()).isPresent()) {
                        sessionRepository.save(sessionEntity);
                        saveSessionExercises(exerciseEntityList, sessionEntity, session.getTrainingVariables());
                        return new ReturnSession(sessionEntity.getSessionId(), "Session successfully created");
                    } else {
                        return new ReturnSession(sessionEntity.getSessionId(), "The sessionId was already created");
                    }
                })
                .toList();
    }

    private void saveSessionExercises(List<ExerciseEntity> exerciseEntityList,
                                      SessionEntity sessionEntity,
                                      List<TrainingVariable> trainingVariableList) {
        exerciseEntityList.stream()
                .map(exerciseEntity -> new SessionExerciseEntity(
                        sessionEntity.getSessionId(), exerciseEntity.getExerciseId(), null,
                        sessionEntity, exerciseEntity))
                .forEach(sessionExerciseEntity -> {
                    sessionExerciseRepository.save(sessionExerciseEntity);
                    List<TrainingVariable> filteredTrainingVariableList =
                            filterTrainingVariablesByExerciseId(trainingVariableList, sessionExerciseEntity.getExerciseId());
                    saveTrainingVariables(filteredTrainingVariableList, sessionExerciseEntity);
                });
    }

    private List<TrainingVariable> filterTrainingVariablesByExerciseId(
            List<TrainingVariable> trainingVariableList, Integer exerciseId) {
        return trainingVariableList.stream()
                .filter(trainingVariable -> Objects.equals(trainingVariable.getExerciseId(), exerciseId))
                .toList();
    }

    private void saveTrainingVariables(List<TrainingVariable> trainingVariableList,
                                       SessionExerciseEntity sessionExerciseEntity) {
        trainingVariableList.stream()
                .map(trainingVariable -> new TrainingVariablesEntity(
                        trainingVariable.getSetNumber(),
                        sessionExerciseEntity,
                        trainingVariable.getWeight().setScale(3, RoundingMode.HALF_UP),
                        trainingVariable.getRepetitions(),
                        trainingVariable.getRir()))
                .forEach(trainingVariablesRepository::save);
    }

    private List<Session> filterSessionsCreated(List<Session> sessionList, List<ReturnSession> returnSessionList) {
        List<Integer> validSessionIds = returnSessionList.stream()
                .filter(returnSession -> "Session successfully created".equals(returnSession.getStatus()))
                .map(ReturnSession::getSessionId)
                .toList();
        return sessionList.stream()
                .filter(session -> validSessionIds.contains(session.getSessionId()))
                .toList();
    }

    @Transactional
    private void saveTrainingVolume(List<Session> sessionList) {
        sessionList.forEach(session -> {
            List<SessionExerciseEntity> sessionExerciseEntityList =
                    sessionExerciseRepository.findBySessionId(session.getSessionId());
            sessionExerciseEntityList.forEach(sessionExerciseEntity -> {
                BigDecimal trainingVolume =
                        calculateTrainingVolume(trainingVariablesRepository.findBySessionExercise(sessionExerciseEntity));
                sessionExerciseEntity.setTrainingVolume(trainingVolume);
                sessionExerciseRepository.save(sessionExerciseEntity);
            });
        });
    }

    private BigDecimal calculateTrainingVolume(List<TrainingVariablesEntity> trainingVariablesEntityList) {
        return trainingVariablesEntityList.stream()
                .map(trainingVariablesEntity -> trainingVariablesEntity.getWeight()
                        .multiply(new BigDecimal(trainingVariablesEntity.getRepetitions())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(10, RoundingMode.HALF_UP);
    }

    private void sendTrainingSessionEmail(String destinationEmail, List<Session> sessionList) {
        String user = sessionList.get(0).getUsername();
        sessionList.stream()
                .map(session -> createEmailMessage(destinationEmail, user, session))
                .forEach(emailSender::send);
    }

    private SimpleMailMessage createEmailMessage(String destinationEmail, String user, Session session) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(destinationEmail);
        message.setSubject("Training Diary App");
        message.setText("User " + user + " has registered a new training session on " + session.getSessionDate().toString());
        return message;
    }

}