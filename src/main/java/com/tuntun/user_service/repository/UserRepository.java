package com.tuntun.user_service.repository;

import com.tuntun.user_service.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<Users, Long> , JpaSpecificationExecutor<Users> {
    Optional<Users> findByPhoneNumberAndIsActiveTrue(String phoneNumber);
    Optional<Users> findByPhoneNumber(String phoneNumber);

    Optional<Users> findByUsernameAndIsActiveTrue(String username);
    Optional<Users> findByUsername(String username);



}
