package com.isia.tfm.service;

import com.isia.tfm.entity.ExerciseEntity;
import com.isia.tfm.model.ReturnSession;
import com.isia.tfm.model.Session;

import java.util.List;

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
