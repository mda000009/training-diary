package com.isia.tfm.controller;

import com.isia.tfm.api.ExerciseManagementApi;
import com.isia.tfm.model.CreateExercises200Response;
import com.isia.tfm.model.CreateExercisesRequest;
import com.isia.tfm.service.ExerciseManagementService;
import org.apache.commons.lang3.tuple.Pair;
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
    public ResponseEntity<CreateExercises200Response> createExercises(CreateExercisesRequest createExercisesRequest) {
        Pair<CreateExercises200Response, String> response = exerciseManagementService.createExercises(createExercisesRequest);

        HttpStatus status = switch (response.getRight()) {
            case "200" -> HttpStatus.OK;
            case "201" -> HttpStatus.CREATED;
            case "207" -> HttpStatus.MULTI_STATUS;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return new ResponseEntity<>(response.getLeft(), status);
    }

}
