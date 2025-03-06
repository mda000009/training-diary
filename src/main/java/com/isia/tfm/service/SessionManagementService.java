package com.isia.tfm.service;

import com.isia.tfm.model.CreateSessions201Response;
import com.isia.tfm.model.CreateSessionsRequest;

public interface SessionManagementService {

    /**
     *
     * @param createSessionsRequest the create sessions request
     * @return {@link CreateSessions201Response}
     */
    CreateSessions201Response createSessions(CreateSessionsRequest createSessionsRequest);

}
