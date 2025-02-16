package com.isia.tfm.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "TRAINING_VARIABLES")
@Data
public class TrainingVariablesEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "SET_NUMBER", nullable = false)
    private Integer setNumber;

    @ManyToOne
    @JoinColumn(name = "SESSION_ID", referencedColumnName = "SESSION_ID", nullable = false)
    private SessionExerciseEntity sessionExerciseBySessionEntity;

    @ManyToOne
    @JoinColumn(name = "EXERCISE_ID", referencedColumnName = "EXERCISE_ID", nullable = false)
    private SessionExerciseEntity sessionExerciseByExerciseEntity;

    @Column(name = "WEIGHT")
    private Double weight;

    @Column(name = "REPETITIONS")
    private Integer repetitions;

    @Column(name = "RIR")
    private Integer rir;

}
