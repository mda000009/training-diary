package com.isia.tfm.controller;

import com.isia.tfm.model.CreateExercises200Response;
import com.isia.tfm.model.CreateExercises200ResponseData;
import com.isia.tfm.model.CreateExercisesRequest;
import com.isia.tfm.model.ReturnExercise;
import com.isia.tfm.service.ExerciseManagementService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureObservability
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExerciseManagementControllerTest {

    @InjectMocks
    private ExerciseManagementController exerciseManagementController;
    @Mock
    private ExerciseManagementService exerciseManagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createExercises201() {
        CreateExercisesRequest createExercisesRequest = TestUtils.readMockFile("exercises", CreateExercisesRequest.class);
        CreateExercises200Response createExercises200Response = new CreateExercises200Response();
        CreateExercises200ResponseData data = new CreateExercises200ResponseData();
        List<ReturnExercise> returnExerciseList = new ArrayList<>();
        returnExerciseList.add(new ReturnExercise(1, "Exercise successfully created"));
        returnExerciseList.add(new ReturnExercise(2, "Exercise successfully created"));
        data.setExercises(returnExerciseList);
        createExercises200Response.setData(data);

        when(exerciseManagementService.createExercises(any(CreateExercisesRequest.class))).thenReturn(Pair.of(createExercises200Response, "201"));

        ResponseEntity<CreateExercises200Response> response = exerciseManagementController.createExercises(createExercisesRequest);

        ResponseEntity<CreateExercises200Response> expectedResponse = new ResponseEntity<>(createExercises200Response, HttpStatus.CREATED);

        assertEquals(expectedResponse, response);
    }

    @Test
    void createExercises200() {
        CreateExercisesRequest createExercisesRequest = TestUtils.readMockFile("exercises", CreateExercisesRequest.class);
        CreateExercises200Response createExercises200Response = new CreateExercises200Response();
        CreateExercises200ResponseData data = new CreateExercises200ResponseData();
        List<ReturnExercise> returnExerciseList = new ArrayList<>();
        returnExerciseList.add(new ReturnExercise(1, "The exerciseId was already created"));
        returnExerciseList.add(new ReturnExercise(2, "The exerciseId was already created"));
        data.setExercises(returnExerciseList);
        createExercises200Response.setData(data);

        when(exerciseManagementService.createExercises(any(CreateExercisesRequest.class))).thenReturn(Pair.of(createExercises200Response, "200"));

        ResponseEntity<CreateExercises200Response> response = exerciseManagementController.createExercises(createExercisesRequest);

        ResponseEntity<CreateExercises200Response> expectedResponse = new ResponseEntity<>(createExercises200Response, HttpStatus.OK);

        assertEquals(expectedResponse, response);
    }

    @Test
    void createExercises207() {
        CreateExercisesRequest createExercisesRequest = TestUtils.readMockFile("exercises", CreateExercisesRequest.class);
        CreateExercises200Response createExercises200Response = new CreateExercises200Response();
        CreateExercises200ResponseData data = new CreateExercises200ResponseData();
        List<ReturnExercise> returnExerciseList = new ArrayList<>();
        returnExerciseList.add(new ReturnExercise(1, "Exercise successfully created"));
        returnExerciseList.add(new ReturnExercise(2, "The exerciseId was already created"));
        data.setExercises(returnExerciseList);
        createExercises200Response.setData(data);

        when(exerciseManagementService.createExercises(any(CreateExercisesRequest.class))).thenReturn(Pair.of(createExercises200Response, "207"));

        ResponseEntity<CreateExercises200Response> response = exerciseManagementController.createExercises(createExercisesRequest);

        ResponseEntity<CreateExercises200Response> expectedResponse = new ResponseEntity<>(createExercises200Response, HttpStatus.MULTI_STATUS);

        assertEquals(expectedResponse, response);
    }

}
