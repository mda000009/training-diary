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
     * @param session the session
     * @param exerciseEntityList the exercise entity list
     * @return {@link ReturnSession}
     */
    ReturnSession saveSession(Session session, List<ExerciseEntity> exerciseEntityList);

    /**
     *
     * @param session the session
     */
    void saveTrainingVolume(Session session);

}
