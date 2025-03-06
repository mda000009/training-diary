package com.isia.tfm.repository;

import com.isia.tfm.entity.SessionExerciseEntity;
import com.isia.tfm.entity.SessionExercisePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Session exercise repository.
 */
@Repository
public interface SessionExerciseRepository extends JpaRepository<SessionExerciseEntity, SessionExercisePK> {

    /**
     * Retrieves session exercise entity list finding by session id.
     *
     * @param sessionId the session id.
     *
     * @return {@link List<SessionExerciseEntity>}
     */
    List<SessionExerciseEntity> findBySessionId(Integer sessionId);

}
