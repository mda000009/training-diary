package com.isia.tfm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isia.tfm.entity.ExerciseEntity;
import com.isia.tfm.model.CreateExercises201Response;
import com.isia.tfm.model.CreateExercisesRequest;
import com.isia.tfm.model.Exercise;
import com.isia.tfm.repository.ExerciseRepository;
import com.isia.tfm.service.ExerciseManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExerciseManagementServiceImpl implements ExerciseManagementService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ExerciseRepository exerciseRepository;

    @Override
    public CreateExercises201Response createExercises(CreateExercisesRequest createExercisesRequest) {
        CreateExercises201Response createExercises201Response = new CreateExercises201Response();
        for (Exercise exercise : createExercisesRequest.getExercises()) {
            ExerciseEntity exerciseEntity = objectMapper.convertValue(exercise, ExerciseEntity.class);
            exerciseRepository.save(exerciseEntity);
        }
        log.debug("Saved exercises");
        createExercises201Response.setMessage("Exercises successfully created.");
        return createExercises201Response;
    }
}
