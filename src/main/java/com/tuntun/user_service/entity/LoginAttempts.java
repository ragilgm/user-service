package com.tuntun.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "login_attempts")
public class LoginAttempts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @Column(name = "device", nullable = false, length = 255)
    private String device;

    @Column(name = "device_identifier", nullable = false, length = 255)
    private String deviceIdentifier;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "attempt_time", nullable = false, updatable = false)
    private LocalDateTime attemptTime;

    @Column(name = "success")
    private Boolean success;


    @PrePersist
    protected void onCreate() {
        attemptTime = LocalDateTime.now();
    }


}
