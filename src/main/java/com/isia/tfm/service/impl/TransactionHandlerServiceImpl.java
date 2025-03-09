package com.isia.tfm.service.impl;

import com.isia.tfm.entity.*;
import com.isia.tfm.exception.CustomException;
import com.isia.tfm.model.ReturnSession;
import com.isia.tfm.model.Session;
import com.isia.tfm.model.TrainingVariable;
import com.isia.tfm.repository.*;
import com.isia.tfm.service.TransactionHandlerService;
import com.isia.tfm.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
public class TransactionHandlerServiceImpl implements TransactionHandlerService {

    ApplicationUserRepository applicationUserRepository;
    SessionRepository sessionRepository;
    SessionExerciseRepository sessionExerciseRepository;
    TrainingVariablesRepository trainingVariablesRepository;

    public TransactionHandlerServiceImpl(ApplicationUserRepository applicationUserRepository,
                                        SessionRepository sessionRepository,
                                        SessionExerciseRepository sessionExerciseRepository,
                                        TrainingVariablesRepository trainingVariablesRepository) {
        this.applicationUserRepository = applicationUserRepository;
        this.sessionRepository = sessionRepository;
        this.sessionExerciseRepository = sessionExerciseRepository;
        this.trainingVariablesRepository = trainingVariablesRepository;
    }

    @Override
    @Transactional
    public List<ReturnSession> saveSessions(List<Session> sessionList, List<ExerciseEntity> exerciseEntityList) {
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
                            Utils.filterTrainingVariablesByExerciseId(trainingVariableList, sessionExerciseEntity.getExerciseId());
                    saveTrainingVariables(filteredTrainingVariableList, sessionExerciseEntity);
                });
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

    @Override
    @Transactional
    public void saveTrainingVolume(List<Session> sessionList) {
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

}
