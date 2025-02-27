package com.isia.tfm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isia.tfm.entity.ExerciseEntity;
import com.isia.tfm.model.CreateExercises201Response;
import com.isia.tfm.model.CreateExercisesRequest;
import com.isia.tfm.model.ReturnExercise;
import com.isia.tfm.repository.ExerciseRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureObservability
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExerciseManagementServiceImplTest {
    @InjectMocks
    private ExerciseManagementServiceImpl exerciseManagementServiceImpl;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ExerciseRepository exerciseRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createExercises() {
        CreateExercisesRequest createExercisesRequest = TestUtils.readMockFile("exercises", CreateExercisesRequest.class);
        ExerciseEntity exerciseEntity = new ExerciseEntity(1, "Bench Press");

        when(objectMapper.convertValue(createExercisesRequest.getExercises().get(0), ExerciseEntity.class))
                .thenReturn(exerciseEntity);
        when(exerciseRepository.save(any(ExerciseEntity.class))).thenReturn(exerciseEntity);
        when(exerciseRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        CreateExercises201Response response = exerciseManagementServiceImpl.createExercises(createExercisesRequest);

        CreateExercises201Response expectedResponse = new CreateExercises201Response();
        expectedResponse.setExercises(Collections.singletonList(new ReturnExercise(1, "Exercise successfully created")));

        assertEquals(expectedResponse, response);
    }
}
