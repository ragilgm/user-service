package com.tuntun.user_service.repository;

import com.tuntun.interface_center.user_interface.enums.OTPActionType;
import com.tuntun.interface_center.user_interface.enums.OtpStatus;
import com.tuntun.user_service.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<Otp, Long> , JpaSpecificationExecutor<Otp> {



    Optional<Otp> findByPhoneNumberAndActionTypeAndOtpStatus(String phoneNumber, OTPActionType actionType, OtpStatus used);


    Optional<Otp> findByPhoneNumberAndUniqueIdentifierAndActionType(String phoneNumber, String uniqueIdentifier, OTPActionType actionType);
}
