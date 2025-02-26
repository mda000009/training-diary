package com.isia.tfm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "TRAINING_VARIABLES")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingVariablesEntity implements Serializable {

    public TrainingVariablesEntity(Integer setNumber, SessionExerciseEntity sessionExercise,
                                   BigDecimal weight, Integer repetitions, Integer rir) {
        this.setNumber = setNumber;
        this.sessionExercise = sessionExercise;
        this.weight = weight;
        this.repetitions = repetitions;
        this.rir = rir;
    }

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "SET_NUMBER", nullable = false)
    private Integer setNumber;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SESSION_ID", referencedColumnName = "SESSION_ID"),
            @JoinColumn(name = "EXERCISE_ID", referencedColumnName = "EXERCISE_ID")
    })
    private SessionExerciseEntity sessionExercise;

    @Column(name = "WEIGHT", nullable = false)
    private BigDecimal weight;

    @Column(name = "REPETITIONS", nullable = false)
    private Integer repetitions;

    @Column(name = "RIR")
    private Integer rir;

}
