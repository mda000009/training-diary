package com.isia.tfm.controller;

import com.isia.tfm.api.ExerciseManagementApi;
import com.isia.tfm.model.CreateExercises201Response;
import com.isia.tfm.model.CreateExercisesRequest;
import com.isia.tfm.service.ExerciseManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exercise management controller
 */
@RestController
@RequestMapping({"/training-diary/v1"})
public class ExerciseManagementController implements ExerciseManagementApi {

    ExerciseManagementService exerciseManagementService;

    public ExerciseManagementController(ExerciseManagementService exerciseManagementService) {
        this.exerciseManagementService = exerciseManagementService;
    }

    @Override
    public ResponseEntity<CreateExercises201Response> createExercises(CreateExercisesRequest createExercisesRequest) {
        CreateExercises201Response response = exerciseManagementService.createExercises(createExercisesRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
