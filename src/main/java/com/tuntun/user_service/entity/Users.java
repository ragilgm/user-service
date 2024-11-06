package com.tuntun.user_service.entity;

import com.tuntun.datasource_starter.converter.EncryptionConverter;
import com.tuntun.interface_center.user_interface.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "users")
public class Users extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;

    @Convert(converter = EncryptionConverter.class)
    private String username;

    @Convert(converter = EncryptionConverter.class)
    private String phoneNumber;

    private String email;

    private String passwordHash;

    private String transactionPasswordHash;

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

}
