package com.isia.tfm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isia.tfm.entity.ExerciseEntity;
import com.isia.tfm.model.CreateExercises200Response;
import com.isia.tfm.model.CreateExercises200ResponseData;
import com.isia.tfm.model.CreateExercisesRequest;
import com.isia.tfm.model.ReturnExercise;
import com.isia.tfm.repository.ExerciseRepository;
import com.isia.tfm.testutils.TestUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureObservability
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExerciseManagementServiceImplTest {

    @InjectMocks
    private ExerciseManagementServiceImpl exerciseManagementServiceImpl;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ExerciseRepository exerciseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createExercises200() {
        CreateExercisesRequest createExercisesRequest = TestUtils.readMockFile("exercises", CreateExercisesRequest.class);
        ExerciseEntity exerciseEntity1 = new ExerciseEntity(1, "Bench Press");
        ExerciseEntity exerciseEntity2 = new ExerciseEntity(2, "Squat");

        when(objectMapper.convertValue(createExercisesRequest.getExercises().get(0), ExerciseEntity.class))
                .thenReturn(exerciseEntity1);
        when(objectMapper.convertValue(createExercisesRequest.getExercises().get(1), ExerciseEntity.class))
                .thenReturn(exerciseEntity2);
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exerciseEntity1));
        when(exerciseRepository.findById(2)).thenReturn(Optional.of(exerciseEntity2));

        Pair<CreateExercises200Response, String> response = exerciseManagementServiceImpl.createExercises(createExercisesRequest);

        CreateExercises200Response createExercises200Response = new CreateExercises200Response();
        CreateExercises200ResponseData data = new CreateExercises200ResponseData();
        List<ReturnExercise> returnExerciseList = new ArrayList<>();
        returnExerciseList.add(new ReturnExercise(1, "The exerciseId was already created"));
        returnExerciseList.add(new ReturnExercise(2, "The exerciseId was already created"));
        data.setExercises(returnExerciseList);
        createExercises200Response.setData(data);
        Pair<CreateExercises200Response, String> expectedResponse = Pair.of(createExercises200Response, "200");

        assertEquals(expectedResponse, response);
    }

    @Test
    void createExercises201() {
        CreateExercisesRequest createExercisesRequest = TestUtils.readMockFile("exercises", CreateExercisesRequest.class);
        ExerciseEntity exerciseEntity1 = new ExerciseEntity(1, "Bench Press");
        ExerciseEntity exerciseEntity2 = new ExerciseEntity(2, "Squat");

        when(objectMapper.convertValue(createExercisesRequest.getExercises().get(0), ExerciseEntity.class))
                .thenReturn(exerciseEntity1);
        when(objectMapper.convertValue(createExercisesRequest.getExercises().get(1), ExerciseEntity.class))
                .thenReturn(exerciseEntity2);
        when(exerciseRepository.save(exerciseEntity1)).thenReturn(exerciseEntity1);
        when(exerciseRepository.save(exerciseEntity2)).thenReturn(exerciseEntity2);
        when(exerciseRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        Pair<CreateExercises200Response, String> response = exerciseManagementServiceImpl.createExercises(createExercisesRequest);

        CreateExercises200Response createExercises200Response = new CreateExercises200Response();
        CreateExercises200ResponseData data = new CreateExercises200ResponseData();
        List<ReturnExercise> returnExerciseList = new ArrayList<>();
        returnExerciseList.add(new ReturnExercise(1, "Exercise successfully created"));
        returnExerciseList.add(new ReturnExercise(2, "Exercise successfully created"));
        data.setExercises(returnExerciseList);
        createExercises200Response.setData(data);
        Pair<CreateExercises200Response, String> expectedResponse = Pair.of(createExercises200Response, "201");

        assertEquals(expectedResponse, response);
    }

    @Test
    void createExercises207() {
        CreateExercisesRequest createExercisesRequest = TestUtils.readMockFile("exercises", CreateExercisesRequest.class);
        ExerciseEntity exerciseEntity1 = new ExerciseEntity(1, "Bench Press");
        ExerciseEntity exerciseEntity2 = new ExerciseEntity(2, "Squat");

        when(objectMapper.convertValue(createExercisesRequest.getExercises().get(0), ExerciseEntity.class))
                .thenReturn(exerciseEntity1);
        when(objectMapper.convertValue(createExercisesRequest.getExercises().get(1), ExerciseEntity.class))
                .thenReturn(exerciseEntity2);
        when(exerciseRepository.save(exerciseEntity1)).thenReturn(exerciseEntity1);
        when(exerciseRepository.findById(1)).thenReturn(Optional.empty());
        when(exerciseRepository.findById(2)).thenReturn(Optional.of(exerciseEntity2));

        Pair<CreateExercises200Response, String> response = exerciseManagementServiceImpl.createExercises(createExercisesRequest);

        CreateExercises200Response createExercises200Response = new CreateExercises200Response();
        CreateExercises200ResponseData data = new CreateExercises200ResponseData();
        List<ReturnExercise> returnExerciseList = new ArrayList<>();
        returnExerciseList.add(new ReturnExercise(1, "Exercise successfully created"));
        returnExerciseList.add(new ReturnExercise(2, "The exerciseId was already created"));
        data.setExercises(returnExerciseList);
        createExercises200Response.setData(data);
        Pair<CreateExercises200Response, String> expectedResponse = Pair.of(createExercises200Response, "207");

        assertEquals(expectedResponse, response);
    }

}
