package com.isia.tfm.controller;

import com.isia.tfm.api.SessionManagementApi;
import com.isia.tfm.model.CreateSessions201Response;
import com.isia.tfm.model.CreateSessionsRequest;
import com.isia.tfm.service.SessionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Session management controller
 */
@RestController
@RequestMapping({"/training-diary/v1"})
public class SessionManagementController implements SessionManagementApi {

    @Autowired
    private SessionManagementService sessionManagementService;

    @Override
    public ResponseEntity<CreateSessions201Response> createSessions(CreateSessionsRequest createSessionsRequest) {
        CreateSessions201Response response = sessionManagementService.createSessions(createSessionsRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
