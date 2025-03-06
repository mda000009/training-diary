package com.isia.tfm.service;

import com.isia.tfm.model.CreateExercises201Response;
import com.isia.tfm.model.CreateExercisesRequest;

/**
 * Service interface for managing exercises.
 */
public interface ExerciseManagementService {

    /**
     *
     * @param createExercisesRequest the create exercises request
     * @return {@link CreateExercises201Response}
     */
    CreateExercises201Response createExercises(CreateExercisesRequest createExercisesRequest);

}
