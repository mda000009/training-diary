package com.isia.tfm.repository;

import com.isia.tfm.entity.SessionExerciseEntity;
import com.isia.tfm.entity.SessionExercisePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Session exercise repository.
 */
@Repository
public interface SessionExerciseRepository extends JpaRepository<SessionExerciseEntity, SessionExercisePK> {}
