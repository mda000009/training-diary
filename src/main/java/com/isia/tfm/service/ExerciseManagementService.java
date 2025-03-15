package com.isia.tfm.service;

import com.isia.tfm.model.CreateExercises200Response;
import com.isia.tfm.model.CreateExercisesRequest;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Service interface for managing exercises.
 */
public interface ExerciseManagementService {

    /**
     *
     * @param createExercisesRequest the create exercises request
     * @return {@link Pair} with the createExercises200Response and the HTTP status code
     */
    Pair<CreateExercises200Response, String> createExercises(CreateExercisesRequest createExercisesRequest);

}
