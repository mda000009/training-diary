package com.isia.tfm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isia.tfm.entity.ExerciseEntity;
import com.isia.tfm.model.*;
import com.isia.tfm.repository.ExerciseRepository;
import com.isia.tfm.service.ExerciseManagementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExerciseManagementServiceImpl implements ExerciseManagementService {

    private static final String EXERCISE_CREATED = "Exercise successfully created";
    private static final String EXERCISE_EXISTS = "The exerciseId was already created";

    ObjectMapper objectMapper;
    ExerciseRepository exerciseRepository;

    public ExerciseManagementServiceImpl(ObjectMapper objectMapper, ExerciseRepository exerciseRepository) {
        this.objectMapper = objectMapper;
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public Pair<CreateExercises200Response, String> createExercises(CreateExercisesRequest createExercisesRequest) {
        List<ReturnExercise> returnExerciseList = createExercisesRequest.getExercises().stream()
                .map(exercise -> {
                    ExerciseEntity exerciseEntity = objectMapper.convertValue(exercise, ExerciseEntity.class);
                    boolean createdExercise = exerciseRepository.findById(exerciseEntity.getExerciseId()).isPresent();
                    if (!createdExercise) {
                        exerciseRepository.save(exerciseEntity);
                        return new ReturnExercise(exerciseEntity.getExerciseId(), EXERCISE_CREATED);
                    } else {
                        return new ReturnExercise(exerciseEntity.getExerciseId(), EXERCISE_EXISTS);
                    }
                })
                .toList();

        String status = determineStatus(returnExerciseList);

        CreateExercises200ResponseData data = new CreateExercises200ResponseData();
        data.setExercises(returnExerciseList);
        CreateExercises200Response createExercises200Response = new CreateExercises200Response();
        createExercises200Response.setData(data);

        return Pair.of(createExercises200Response, status);
    }

    private String determineStatus(List<ReturnExercise> returnExerciseList) {
        boolean allCreated = returnExerciseList.stream()
                .allMatch(exercise -> EXERCISE_CREATED.equals(exercise.getMessage()));

        boolean allAlreadyCreated = returnExerciseList.stream()
                .allMatch(exercise -> EXERCISE_EXISTS.equals(exercise.getMessage()));

        if (allCreated) {
            return "201";
        } else if (allAlreadyCreated) {
            return "200";
        } else {
            return "207";
        }
    }

}
