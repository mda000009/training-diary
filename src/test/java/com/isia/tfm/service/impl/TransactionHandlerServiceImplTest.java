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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@AutoConfigureObservability
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionHandlerServiceImplTest {

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveSessions() {
        User user = TestUtils.readMockFile("user", User.class);
        ApplicationUserEntity applicationUserEntity = new ApplicationUserEntity(user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getPassword(), user.getBirthday(), "Male", user.getEmail(),
                user.getPhoneNumber(), LocalDateTime.now());
        CreateSessionsRequest createSessionsRequest = TestUtils.readMockFile("sessions", CreateSessionsRequest.class);
        SessionEntity sessionEntity = new SessionEntity(1, "Session 1", LocalDate.now(), applicationUserEntity);
        ExerciseEntity exerciseEntity = new ExerciseEntity(1, "Bench Press");
        SessionExerciseEntity sessionExerciseEntity = new SessionExerciseEntity(1, 1, null, sessionEntity, exerciseEntity);
        TrainingVariable trainingVariable = createSessionsRequest.getSessions().get(0).getTrainingVariables().get(0);
        TrainingVariablesEntity trainingVariablesEntity = new TrainingVariablesEntity(
                trainingVariable.getSetNumber(), sessionExerciseEntity,
                trainingVariable.getWeight(), trainingVariable.getRepetitions(), trainingVariable.getRir());

        when(applicationUserRepository.findById(user.getUsername())).thenReturn(Optional.of(applicationUserEntity));
        when(sessionRepository.save(any(SessionEntity.class))).thenReturn(sessionEntity);
        when(sessionRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        when(sessionExerciseRepository.save(any(SessionExerciseEntity.class))).thenReturn(sessionExerciseEntity);
        when(trainingVariablesRepository.save(any(TrainingVariablesEntity.class))).thenReturn(trainingVariablesEntity);

        List<ReturnSession> response = transactionHandlerServiceImpl.saveSessions(
                createSessionsRequest.getSessions(), Collections.singletonList(new ExerciseEntity(1, "Bench Press")));

        List<ReturnSession> expectedResponse = Collections.singletonList(new ReturnSession(1, "Session successfully created"));

        assertEquals(expectedResponse, response);
    }

    @Test
    void saveTrainingVolume() {
        User user = TestUtils.readMockFile("user", User.class);
        ApplicationUserEntity applicationUserEntity = new ApplicationUserEntity(user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getPassword(), user.getBirthday(), "Male", user.getEmail(),
                user.getPhoneNumber(), LocalDateTime.now());
        CreateSessionsRequest createSessionsRequest = TestUtils.readMockFile("sessions", CreateSessionsRequest.class);
        SessionEntity sessionEntity = new SessionEntity(1, "Session 1", LocalDate.now(), applicationUserEntity);
        ExerciseEntity exerciseEntity = new ExerciseEntity(1, "Bench Press");
        SessionExerciseEntity sessionExerciseEntity = new SessionExerciseEntity(1, 1, null, sessionEntity, exerciseEntity);
        TrainingVariable trainingVariable = createSessionsRequest.getSessions().get(0).getTrainingVariables().get(0);
        TrainingVariablesEntity trainingVariablesEntity = new TrainingVariablesEntity(
                trainingVariable.getSetNumber(), sessionExerciseEntity,
                trainingVariable.getWeight(), trainingVariable.getRepetitions(), trainingVariable.getRir());

        when(sessionExerciseRepository.findBySessionId(1)).thenReturn(Collections.singletonList(sessionExerciseEntity));
        when(trainingVariablesRepository.findBySessionExercise(sessionExerciseEntity)).thenReturn(Collections.singletonList(trainingVariablesEntity));
        when(sessionExerciseRepository.save(any(SessionExerciseEntity.class))).thenReturn(sessionExerciseEntity);

        transactionHandlerServiceImpl.saveTrainingVolume(createSessionsRequest.getSessions());

        verify(sessionExerciseRepository, times(1)).findBySessionId(1);
        verify(trainingVariablesRepository, times(1)).findBySessionExercise(sessionExerciseEntity);
        verify(sessionExerciseRepository, times(1)).save(sessionExerciseEntity);
    }

    @Test
    void saveSessionsErrorUser() {
        CreateSessionsRequest createSessionsRequest = TestUtils.readMockFile("sessions", CreateSessionsRequest.class);

        when(applicationUserRepository.findById(createSessionsRequest.getSessions().get(0).getUsername())).thenReturn(Optional.empty());

        CustomException e = assertThrows(CustomException.class, () -> {
            transactionHandlerServiceImpl.saveSessions(
                    createSessionsRequest.getSessions(), Collections.singletonList(new ExerciseEntity(1, "Bench Press")));
        });

        assertEquals("User with username juanpereza not found", e.getMessage());
    }

}
