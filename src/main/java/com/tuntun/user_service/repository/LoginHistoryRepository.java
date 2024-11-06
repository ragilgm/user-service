package com.tuntun.user_service.repository;

import com.tuntun.user_service.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> , JpaSpecificationExecutor<LoginHistory> {

    List<LoginHistory> findByUserId(Long userId);

    Optional<LoginHistory> findFirstByUserIdOrderByIdDesc(Long userId);


}
