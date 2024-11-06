package com.tuntun.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "login_history")
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @Column(name = "device", nullable = false, length = 255)
    private String device;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "login_time", nullable = false, updatable = false)
    private LocalDateTime loginTime;


    @PrePersist
    protected void onCreate() {
        loginTime = LocalDateTime.now();
    }
}
