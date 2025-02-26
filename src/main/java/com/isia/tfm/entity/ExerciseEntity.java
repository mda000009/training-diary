package com.isia.tfm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EXERCISES")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseEntity {

    @Id
    @Column(name = "EXERCISE_ID", nullable = false)
    private Integer exerciseId;

    @Column(name = "EXERCISE_NAME", nullable = false)
    private String exerciseName;

}
