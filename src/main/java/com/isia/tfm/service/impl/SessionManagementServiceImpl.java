package com.isia.tfm.service.impl;

import com.isia.tfm.entity.*;
import com.isia.tfm.exception.CustomException;
import com.isia.tfm.model.CreateSessions201Response;
import com.isia.tfm.model.CreateSessionsRequest;
import com.isia.tfm.model.Session;
import com.isia.tfm.model.TrainingVariable;
import com.isia.tfm.repository.*;
import com.isia.tfm.service.SessionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SessionManagementServiceImpl implements SessionManagementService {
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private ApplicationUserRespository applicationUserRespository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SessionExerciseRepository sessionExerciseRepository;
    @Autowired
    private TrainingVariablesRepository trainingVariablesRepository;

    @Override
    @Transactional
    public CreateSessions201Response createSessions(CreateSessionsRequest createSessionsRequest) {
        CreateSessions201Response createSessions201Response = new CreateSessions201Response();
        List<ExerciseEntity> exerciseEntityList = getExerciseToCreateList(createSessionsRequest);
        List<Session> sessionList = createSessionsRequest.getSessions();
        for (Session session : sessionList) {
            ApplicationUserEntity applicationUserEntity = applicationUserRespository.findById(session.getUsername())
                    .orElseThrow(() -> new CustomException("404", "Not found", "User with ID " + session.getUsername() + " not found"));
            SessionEntity sessionEntity = new SessionEntity(session.getSessionId(), session.getSessionName(), session.getSessionDate(), applicationUserEntity);
            sessionRepository.save(sessionEntity);
            List<TrainingVariable> trainingVariableList = session.getTrainingVariables();
            saveSessionExercise(exerciseEntityList, sessionEntity, trainingVariableList);
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

    private void saveSessionExercise(List<ExerciseEntity> exerciseEntityList,
                                     SessionEntity sessionEntity,
                                     List<TrainingVariable> trainingVariableList) {
        for (ExerciseEntity exerciseEntity : exerciseEntityList) {
            SessionExerciseEntity sessionExerciseEntity = new SessionExerciseEntity(
                    sessionEntity.getSessionId(), exerciseEntity.getExerciseId(), null,
                    null, sessionEntity, exerciseEntity);
            sessionExerciseRepository.save(sessionExerciseEntity);
            saveTrainingVariables(trainingVariableList, sessionExerciseEntity);
        }
    }

    private void saveTrainingVariables(List<TrainingVariable> trainingVariableList,
                                       SessionExerciseEntity sessionExerciseEntity) {
        for (TrainingVariable trainingVariable : trainingVariableList) {
            TrainingVariablesEntity trainingVariablesEntity = new TrainingVariablesEntity(
                    trainingVariable.getSetNumber(), sessionExerciseEntity, sessionExerciseEntity,
                    trainingVariable.getWeight(), trainingVariable.getRepetitions(), trainingVariable.getRir());
            trainingVariablesRepository.save(trainingVariablesEntity);
        }
    }
}