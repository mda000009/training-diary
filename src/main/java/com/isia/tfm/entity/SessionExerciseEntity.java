package com.isia.tfm.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "SESSIONS_EXERCISES")
@Data
public class SessionExerciseEntity {

    @Embeddable
    public static class SessionExerciseId implements Serializable {
        private Integer sessionId;
        private Integer exerciseId;
    }

    @EmbeddedId
    private SessionExerciseId id;

    @ManyToOne
    @JoinColumn(name = "SESSION_ID", referencedColumnName = "SESSION_ID", nullable = false, insertable = false, updatable = false)
    private SessionEntity sessionEntity;

    @ManyToOne
    @JoinColumn(name = "EXERCISE_ID", referencedColumnName = "EXERCISE_ID", nullable = false, insertable = false, updatable = false)
    private ExerciseEntity exerciseEntity;

}
