package com.isia.tfm.controller;

import com.isia.tfm.model.CreateSessions201Response;
import com.isia.tfm.model.CreateSessionsRequest;
import com.isia.tfm.model.ReturnSession;
import com.isia.tfm.service.SessionManagementService;
import com.isia.tfm.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureObservability
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SessionManagementControllerTest {

    @InjectMocks
    private SessionManagementController sessionManagementController;
    @Mock
    private SessionManagementService sessionManagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSessions() {
        CreateSessionsRequest createSessionsRequest = TestUtils.readMockFile("sessions", CreateSessionsRequest.class);
        CreateSessions201Response createSessions201Response = new CreateSessions201Response();
        createSessions201Response.setSessions(Collections.singletonList(new ReturnSession(1, "Session successfully created")));

        when(sessionManagementService.createSessions(any(CreateSessionsRequest.class))).thenReturn(createSessions201Response);

        ResponseEntity<CreateSessions201Response> response = sessionManagementController.createSessions(createSessionsRequest);

        ResponseEntity<CreateSessions201Response> expectedResponse = new ResponseEntity<>(createSessions201Response, HttpStatus.CREATED);

        assertEquals(expectedResponse, response);
    }

}
