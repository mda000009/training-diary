package com.isia.tfm.repository;

import com.isia.tfm.entity.TrainingVariablesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Training variables repository.
 */
@Repository
public interface TrainingVariablesRepository extends JpaRepository<TrainingVariablesEntity, Integer> {}
