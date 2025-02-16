package com.isia.tfm.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Embeddable
@Data
@ToString
@NoArgsConstructor
public class SessionExercisePK implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sessionId;
    private Integer exerciseId;

}
