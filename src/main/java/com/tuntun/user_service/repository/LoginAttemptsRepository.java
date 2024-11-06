package com.tuntun.user_service.repository;

import com.tuntun.user_service.entity.LoginAttempts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginAttemptsRepository extends JpaRepository<LoginAttempts, Long> , JpaSpecificationExecutor<LoginAttempts> {



}
