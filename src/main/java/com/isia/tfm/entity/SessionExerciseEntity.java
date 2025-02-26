package com.isia.tfm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "SESSIONS_EXERCISES")
@Data
@IdClass(SessionExercisePK.class)
@AllArgsConstructor
@NoArgsConstructor
public class SessionExerciseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "SESSION_ID", nullable = false)
    private Integer sessionId;

    @Id
    @Column(name = "EXERCISE_ID", nullable = false)
    private Integer exerciseId;

    @Column(name = "TRAINING_VOLUME")
    private BigDecimal trainingVolume;

    @Column(name = "PROGRESS_IN_TRAINING_VOLUME")
    private BigDecimal progessInTrainingVolume;

    @ManyToOne
    @JoinColumn(name = "SESSION_ID", referencedColumnName = "SESSION_ID", nullable = false, insertable = false, updatable = false)
    private SessionEntity sessionEntity;

    @ManyToOne
    @JoinColumn(name = "EXERCISE_ID", referencedColumnName = "EXERCISE_ID", nullable = false, insertable = false, updatable = false)
    private ExerciseEntity exerciseEntity;

}
