package com.isia.tfm.controller;

import com.isia.tfm.model.ReturnSession;
import com.isia.tfm.model.Session;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
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
    void createSession() {
        Session session = TestUtils.readMockFile("session", Session.class);
        ReturnSession returnSession = new ReturnSession();
        returnSession.setSessionId(1);
        returnSession.setStatus("Session successfully created");
        returnSession.setSavedTrainingVolumeSuccessfully("true");
        returnSession.setSentEmailSuccessfully("true");
        returnSession.setSavedExcelSuccessfully("true");
        String destinationEmail = "0610809824@uma.es";
        String excelFilePath = "C:\\Users\\mda00009\\Desktop\\Excel_Files\\";

        when(sessionManagementService.createSession(anyBoolean(), anyBoolean(), anyBoolean(), any(Session.class), anyString(), anyString())).thenReturn(returnSession);

        boolean flag = true;
        ResponseEntity<ReturnSession> response = sessionManagementController.createSession(
                flag, flag, flag, session, destinationEmail, excelFilePath);

        ResponseEntity<ReturnSession> expectedResponse = new ResponseEntity<>(returnSession, HttpStatus.CREATED);

        assertEquals(expectedResponse, response);
    }

}
