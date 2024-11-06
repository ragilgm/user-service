package com.tuntun.user_service.repository;

import com.tuntun.user_service.entity.DeviceInfo;
import com.tuntun.user_service.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, Long> , JpaSpecificationExecutor<DeviceInfo> {

    List<DeviceInfo> findByUserId(Long userId);
    Optional<DeviceInfo> findFirstByUserIdOrderByIdDesc(Long userId);


}
