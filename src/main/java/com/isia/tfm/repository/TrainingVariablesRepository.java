package com.isia.tfm.repository;

import com.isia.tfm.entity.SessionExerciseEntity;
import com.isia.tfm.entity.TrainingVariablesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Training variables repository.
 */
@Repository
public interface TrainingVariablesRepository extends JpaRepository<TrainingVariablesEntity, Integer> {

    /**
     * Retrieves the training variables list for a session and an exercise.
     *
     * @param sessionExerciseEntity the session exercise entity.
     *
     * @return {@link List<TrainingVariablesEntity>}
     */
    List<TrainingVariablesEntity> findBySessionExercise(SessionExerciseEntity sessionExerciseEntity);

}
