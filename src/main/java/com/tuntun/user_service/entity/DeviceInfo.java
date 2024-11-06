package com.tuntun.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device_info")
public class DeviceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "os", length = 50)
    private String os;

    @Column(name = "os_version", length = 20)
    private String osVersion;

    @Column(name = "app_version", length = 20)
    private String appVersion;

    @Column(name = "device_token")
    private String deviceToken;

    @Column(name = "device_identifier", nullable = false)
    private String deviceIdentifier;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUsed = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUsed = LocalDateTime.now();
    }


}