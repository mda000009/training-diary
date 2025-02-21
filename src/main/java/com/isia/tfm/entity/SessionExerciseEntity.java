package com.isia.tfm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "SESSIONS_EXERCISES")
@Data
@IdClass(SessionExercisePK.class)
@AllArgsConstructor
public class SessionExerciseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "SESSION_ID", nullable = false)
    private Integer sessionId;

    @Id
    @Column(name = "EXERCISE_ID", nullable = false)
    private Integer exerciseId;

    @ManyToOne
    @JoinColumn(name = "SESSION_ID", referencedColumnName = "SESSION_ID", nullable = false, insertable = false, updatable = false)
    private SessionEntity sessionEntity;

    @ManyToOne
    @JoinColumn(name = "EXERCISE_ID", referencedColumnName = "EXERCISE_ID", nullable = false, insertable = false, updatable = false)
    private ExerciseEntity exerciseEntity;

}
