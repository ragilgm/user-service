package com.tuntun.user_service.entity;

import com.tuntun.interface_center.user_interface.enums.OTPActionType;
import com.tuntun.interface_center.user_interface.enums.OtpStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "otp")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_identifier", nullable = false)
    private String uniqueIdentifier;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 10)
    private OTPActionType actionType;

    @Column(name = "otp_code", length = 6, nullable = false)
    private String otpCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OtpStatus otpStatus;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", columnDefinition = "TINYINT")
    private Boolean used;

}
