package com.isia.tfm.service;

import com.isia.tfm.entity.ExerciseEntity;
import com.isia.tfm.model.ReturnSession;
import com.isia.tfm.model.Session;

import java.util.List;

/**
 * Service interface for handling transactions.
 * This interface acts as a proxy to call transactional methods from non-transactional methods in other services.
 */
public interface TransactionHandlerService {

    /**
     *
     * @param sessionList the session list
     * @param exerciseEntityList the exercise entity list
     * @return {@link List<ReturnSession>}
     */
    List<ReturnSession> saveSessions(List<Session> sessionList, List<ExerciseEntity> exerciseEntityList);

    /**
     *
     * @param sessionList the session list
     */
    void saveTrainingVolume(List<Session> sessionList);

}
