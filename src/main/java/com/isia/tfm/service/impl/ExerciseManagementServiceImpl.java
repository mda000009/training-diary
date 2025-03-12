package com.isia.tfm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isia.tfm.entity.ExerciseEntity;
import com.isia.tfm.model.*;
import com.isia.tfm.repository.ExerciseRepository;
import com.isia.tfm.service.ExerciseManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExerciseManagementServiceImpl implements ExerciseManagementService {

    ObjectMapper objectMapper;
    ExerciseRepository exerciseRepository;

    public ExerciseManagementServiceImpl(ObjectMapper objectMapper, ExerciseRepository exerciseRepository) {
        this.objectMapper = objectMapper;
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public CreateExercises201Response createExercises(CreateExercisesRequest createExercisesRequest) {
        List<ReturnExercise> returnExerciseList = createExercisesRequest.getExercises().stream()
                .map(exercise -> {
                    ExerciseEntity exerciseEntity = objectMapper.convertValue(exercise, ExerciseEntity.class);
                    boolean createdExercise = exerciseRepository.findById(exerciseEntity.getExerciseId()).isPresent();
                    if (!createdExercise) {
                        exerciseRepository.save(exerciseEntity);
                        return new ReturnExercise(exerciseEntity.getExerciseId(), "Exercise successfully created");
                    } else {
                        return new ReturnExercise(exerciseEntity.getExerciseId(), "The exerciseId was already created");
                    }
                })
                .toList();
        log.debug("Saved exercises");

        CreateExercises201ResponseData data = new CreateExercises201ResponseData();
        data.setExercises(returnExerciseList);
        CreateExercises201Response createExercises201Response = new CreateExercises201Response();
        createExercises201Response.setData(data);
        return createExercises201Response;
    }

}
