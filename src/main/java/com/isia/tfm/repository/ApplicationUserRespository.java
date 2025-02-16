package com.isia.tfm.repository;

import com.isia.tfm.entity.ApplicationUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationUserRespository extends JpaRepository<ApplicationUserEntity, String> {}
