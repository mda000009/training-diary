package com.isia.tfm.service.impl;

import com.isia.tfm.entity.*;
import com.isia.tfm.exception.CustomException;
import com.isia.tfm.model.*;
import com.isia.tfm.repository.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@AutoConfigureObservability
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionHandlerServiceImplTest {

    @InjectMocks
    private TransactionHandlerServiceImpl transactionHandlerServiceImpl;
    @Mock
    private ApplicationUserRepository applicationUserRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionExerciseRepository sessionExerciseRepository;
    @Mock
    private TrainingVariablesRepository trainingVariablesRepository;

    private static final String FALSE_STRING = "false";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveSession() {
        User user = TestUtils.readMockFile("user", User.class);
        ApplicationUserEntity applicationUserEntity = new ApplicationUserEntity(user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getPassword(), user.getBirthdate(), "Male", user.getEmail(),
                user.getPhoneNumber(), LocalDateTime.now());
        Session session = TestUtils.readMockFile("session", Session.class);
        SessionEntity sessionEntity = new SessionEntity(1, "Session 1", LocalDate.now(), applicationUserEntity);
        ExerciseEntity exerciseEntity = new ExerciseEntity(1, "Bench Press");
        SessionExerciseEntity sessionExerciseEntity = new SessionExerciseEntity(1, 1, null, sessionEntity, exerciseEntity);
        TrainingVariable trainingVariable = session.getTrainingVariables().get(0);
        TrainingVariablesEntity trainingVariablesEntity = new TrainingVariablesEntity(
                trainingVariable.getSetNumber(), sessionExerciseEntity,
                trainingVariable.getWeight(), trainingVariable.getRepetitions(), trainingVariable.getRir());

        when(applicationUserRepository.findById(user.getUsername())).thenReturn(Optional.of(applicationUserEntity));
        when(sessionRepository.save(any(SessionEntity.class))).thenReturn(sessionEntity);
        when(sessionRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        when(sessionExerciseRepository.save(any(SessionExerciseEntity.class))).thenReturn(sessionExerciseEntity);
        when(trainingVariablesRepository.save(any(TrainingVariablesEntity.class))).thenReturn(trainingVariablesEntity);

        ReturnSession response = transactionHandlerServiceImpl.saveSession(
                session, Collections.singletonList(new ExerciseEntity(1, "Bench Press")));

        ReturnSessionData data = new ReturnSessionData(1,"Session successfully created");
        ReturnSessionAdditionalInformation additionalInformation =
                new ReturnSessionAdditionalInformation(FALSE_STRING, FALSE_STRING, FALSE_STRING);
        ReturnSession expectedResponse = new ReturnSession(data, additionalInformation);

        assertEquals(expectedResponse, response);
    }

    @Test
    void saveTrainingVolume() {
        User user = TestUtils.readMockFile("user", User.class);
        ApplicationUserEntity applicationUserEntity = new ApplicationUserEntity(user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getPassword(), user.getBirthdate(), "Male", user.getEmail(),
                user.getPhoneNumber(), LocalDateTime.now());
        Session session = TestUtils.readMockFile("session", Session.class);
        SessionEntity sessionEntity = new SessionEntity(1, "Session 1", LocalDate.now(), applicationUserEntity);
        ExerciseEntity exerciseEntity = new ExerciseEntity(1, "Bench Press");
        SessionExerciseEntity sessionExerciseEntity = new SessionExerciseEntity(1, 1, null, sessionEntity, exerciseEntity);
        TrainingVariable trainingVariable = session.getTrainingVariables().get(0);
        TrainingVariablesEntity trainingVariablesEntity = new TrainingVariablesEntity(
                trainingVariable.getSetNumber(), sessionExerciseEntity,
                trainingVariable.getWeight(), trainingVariable.getRepetitions(), trainingVariable.getRir());

        when(sessionExerciseRepository.findBySessionId(1)).thenReturn(Collections.singletonList(sessionExerciseEntity));
        when(trainingVariablesRepository.findBySessionExercise(sessionExerciseEntity)).thenReturn(Collections.singletonList(trainingVariablesEntity));
        when(sessionExerciseRepository.save(any(SessionExerciseEntity.class))).thenReturn(sessionExerciseEntity);

        transactionHandlerServiceImpl.saveTrainingVolume(session);

        verify(sessionExerciseRepository, times(1)).findBySessionId(1);
        verify(trainingVariablesRepository, times(1)).findBySessionExercise(sessionExerciseEntity);
        verify(sessionExerciseRepository, times(1)).save(sessionExerciseEntity);
    }

    @Test
    void saveSessionErrorUser() {
        Session session = TestUtils.readMockFile("session", Session.class);

        when(applicationUserRepository.findById(session.getUsername())).thenReturn(Optional.empty());

        CustomException e = assertThrows(CustomException.class, () -> callSaveSession(session));

        assertEquals("User with username juanpereza not found", e.getErrorDetails().getError().getMessage());
    }

    @Test
    void saveSessionErrorSession() {
        User user = TestUtils.readMockFile("user", User.class);
        ApplicationUserEntity applicationUserEntity = new ApplicationUserEntity(user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getPassword(), user.getBirthdate(), "Male", user.getEmail(),
                user.getPhoneNumber(), LocalDateTime.now());
        Session session = TestUtils.readMockFile("session", Session.class);
        SessionEntity sessionEntity = new SessionEntity(1, "example", LocalDate.now(), applicationUserEntity);

        when(applicationUserRepository.findById(user.getUsername())).thenReturn(Optional.of(applicationUserEntity));
        when(sessionRepository.findById(any(Integer.class))).thenReturn(Optional.of(sessionEntity));

        CustomException e = assertThrows(CustomException.class, () -> callSaveSession(session));

        assertEquals("The sessionId 1 was already created", e.getErrorDetails().getError().getMessage());
    }

    private void callSaveSession(Session session) {
        transactionHandlerServiceImpl.saveSession(
                session, Collections.singletonList(new ExerciseEntity(1, "Bench Press")));
    }

}
