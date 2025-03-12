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

    private static final String TRUE_STRING = "true";

    @Value("${spring.mail.username}")
    private String senderEmail;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSession() {
        Session session = TestUtils.readMockFile("session", Session.class);
        String destinationEmail = "0610809824@uma.es";
        String excelFilePath = "C:\\Users\\mda00009\\Desktop\\Excel_Files\\";
        ReturnSessionData data = new ReturnSessionData(1,"Session successfully created");
        ReturnSessionAdditionalInformation additionalInformation =
                new ReturnSessionAdditionalInformation(TRUE_STRING, TRUE_STRING, TRUE_STRING);
        ReturnSession expectedResponse = new ReturnSession(data, additionalInformation);

        sessionManagementServiceImpl = spy(sessionManagementServiceImpl);
        try {
            doNothing().when(sessionManagementServiceImpl).saveWorkbookToFile(any(Workbook.class), anyInt(), anyString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        when(exerciseRepository.findAllExerciseIds()).thenReturn(Collections.singletonList(1));
        when(exerciseRepository.findAllById(any(List.class)))
                .thenReturn(Collections.singletonList(new ExerciseEntity(1, "Bench Press")));
        when(transactionHandlerService.saveSession(any(Session.class), anyList()))
                .thenReturn(expectedResponse);

        boolean flag = true;
        ReturnSession response =
                sessionManagementServiceImpl.createSession(flag, flag, flag, session, destinationEmail, excelFilePath);

        verify(transactionHandlerService, times(1)).saveTrainingVolume(any(Session.class));
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));

        assertEquals(expectedResponse, response);
    }

    @Test
    void createSessionErrorExercise() {
        Session session = TestUtils.readMockFile("session", Session.class);
        String destinationEmail = "0610809824@uma.es";
        String excelFilePath = "C:\\Users\\mda00009\\Desktop\\Excel_Files\\";

        when(exerciseRepository.findAllExerciseIds()).thenReturn(Collections.singletonList(2));

        boolean flag = true;
        CustomException e = assertThrows(CustomException.class, () -> {
            sessionManagementServiceImpl.createSession(flag, flag, flag, session, destinationEmail, excelFilePath);
        });

        assertEquals("The exercise with ID 1 is not created", e.getMessage());
    }

}
