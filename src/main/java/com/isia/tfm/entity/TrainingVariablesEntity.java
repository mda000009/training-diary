package com.isia.tfm.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TRAINING_VARIABLES")
@Data
public class TrainingVariablesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "SET_NUMBER", nullable = false)
    private Integer setNumber;

    @EmbeddedId
    private SessionExerciseEntity.SessionExerciseId sessionExerciseId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SESSION_ID", referencedColumnName = "sessionId", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "EXERCISE_ID", referencedColumnName = "exerciseId", nullable = false, insertable = false, updatable = false)
    })
    private SessionExerciseEntity sessionExerciseEntity;

    @Column(name = "WEIGHT")
    private Double weight;

    @Column(name = "REPETITIONS")
    private Integer repetitions;

    @Column(name = "RIR")
    private Integer rir;

}
