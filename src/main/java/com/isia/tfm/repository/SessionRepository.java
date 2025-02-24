package com.isia.tfm.repository;

import com.isia.tfm.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Session repository.
 */
@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Integer> {}
