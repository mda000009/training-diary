package com.isia.tfm.controller;

import com.isia.tfm.model.CreateExercises201Response;
import com.isia.tfm.model.CreateExercisesRequest;
import com.isia.tfm.model.ReturnExercise;
import com.isia.tfm.service.ExerciseManagementService;
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
    void createExercises() {
        CreateExercisesRequest createExercisesRequest = TestUtils.readMockFile("exercises", CreateExercisesRequest.class);
        CreateExercises201Response createExercises201Response = new CreateExercises201Response();
        createExercises201Response.setExercises(Collections.singletonList(new ReturnExercise(1, "Exercise successfully created")));

        when(exerciseManagementService.createExercises(any(CreateExercisesRequest.class))).thenReturn(createExercises201Response);

        ResponseEntity<CreateExercises201Response> response = exerciseManagementController.createExercises(createExercisesRequest);

        ResponseEntity<CreateExercises201Response> expectedResponse = new ResponseEntity<>(createExercises201Response, HttpStatus.CREATED);

        assertEquals(expectedResponse, response);
    }

}
