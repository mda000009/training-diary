package com.isia.tfm.service.impl;

import com.isia.tfm.entity.*;
import com.isia.tfm.exception.CustomException;
import com.isia.tfm.model.*;
import com.isia.tfm.repository.*;
import com.isia.tfm.service.TransactionHandlerService;
import com.isia.tfm.testutils.TestUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@AutoConfigureObservability
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SessionManagementServiceImplTest {

    @InjectMocks
    private SessionManagementServiceImpl sessionManagementServiceImpl;
    @Mock
    private TransactionHandlerService transactionHandlerService;
    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSessions() {
        CreateSessionsRequest createSessionsRequest = TestUtils.readMockFile("sessions", CreateSessionsRequest.class);
        String destinationEmail = "0610809824@uma.es";
        String excelFilePath = "C:\\Users\\mda00009\\Desktop\\Excel_Files\\";

        sessionManagementServiceImpl = spy(sessionManagementServiceImpl);
        try {
            doNothing().when(sessionManagementServiceImpl).saveWorkbookToFile(any(Workbook.class), anyInt(), anyString());
            // doNothing().when(sessionManagementServiceImpl).createExcelFile(anyList(), anyList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        when(exerciseRepository.findAllExerciseIds()).thenReturn(Collections.singletonList(1));
        when(exerciseRepository.findAllById(any(List.class)))
                .thenReturn(Collections.singletonList(new ExerciseEntity(1, "Bench Press")));
        when(transactionHandlerService.saveSessions(anyList(), anyList()))
                .thenReturn(Collections.singletonList(new ReturnSession(1, "Session successfully created")));

        CreateSessions201Response response = sessionManagementServiceImpl.createSessions(Boolean.TRUE, Boolean.TRUE,
                Boolean.TRUE, createSessionsRequest, destinationEmail, excelFilePath);

        verify(transactionHandlerService, times(1)).saveTrainingVolume(anyList());
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));

        CreateSessions201Response expectedResponse = new CreateSessions201Response();
        expectedResponse.setSessions(Collections.singletonList(new ReturnSession(1, "Session successfully created")));
        expectedResponse.setSavedTrainingVolumeSuccessfully("true");
        expectedResponse.setSentEmailSuccessfully("true");
        expectedResponse.setSavedExcelSuccessfully("true");

        assertEquals(expectedResponse, response);
    }

    @Test
    void createSessionsErrorExercise() {
        CreateSessionsRequest createSessionsRequest = TestUtils.readMockFile("sessions", CreateSessionsRequest.class);
        String destinationEmail = "0610809824@uma.es";
        String excelFilePath = "C:\\Users\\mda00009\\Desktop\\Excel_Files\\";

        when(exerciseRepository.findAllExerciseIds()).thenReturn(Collections.singletonList(2));

        CustomException e = assertThrows(CustomException.class, () -> {
            sessionManagementServiceImpl.createSessions(Boolean.TRUE, Boolean.TRUE,
                    Boolean.TRUE, createSessionsRequest, destinationEmail, excelFilePath);
        });

        assertEquals("The exercise with ID 1 is not created", e.getMessage());
    }

}
