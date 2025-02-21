package com.isia.tfm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "SESSIONS")
@Data
@AllArgsConstructor
public class SessionEntity {

    @Id
    @Column(name = "SESSION_ID", nullable = false)
    private Integer sessionId;

    @Column(name = "SESSION_NAME")
    private String sessionName;

    @Column(name = "SESSION_DATE")
    private LocalDate sessionDate;

    @ManyToOne
    @JoinColumn(name = "APPLICATION_USER_USERNAME", referencedColumnName = "USERNAME", nullable = false)
    private ApplicationUserEntity applicationUserEntity;

}
