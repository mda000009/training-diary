package com.isia.tfm.utils;

import com.isia.tfm.model.TrainingVariable;

import java.util.List;
import java.util.Objects;

public class Utils {

    public static List<TrainingVariable> filterTrainingVariablesByExerciseId(
            List<TrainingVariable> trainingVariableList, Integer exerciseId) {
        return trainingVariableList.stream()
                .filter(trainingVariable -> Objects.equals(trainingVariable.getExerciseId(), exerciseId))
                .toList();
    }

}
