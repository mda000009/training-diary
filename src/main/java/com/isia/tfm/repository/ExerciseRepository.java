package com.isia.tfm.repository;

import com.isia.tfm.entity.ExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Exercise repository.
 */
@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseEntity, Integer> {
    /**
     * Retrieves all the primary keys (exerciseId) of all {@link ExerciseEntity}.
     *
     * @return {@link List<Integer>}
     */
    @Query("SELECT e.exerciseId FROM ExerciseEntity e")
    List<Integer> findAllExerciseIds();
}
