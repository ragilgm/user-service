package com.tuntun.user_service.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuntun.interface_center.constants.KafkaTopicConstants;
import com.tuntun.interface_center.dto.ConsumerBaseDTO;
import com.tuntun.interface_center.user_interface.consumer_dto.ConsumerLoginHistoryDTO;
import com.tuntun.interface_center.user_interface.consumer_dto.ConsumerUserUpdateDTO;
import com.tuntun.interface_center.user_interface.dto.LoginUserRequestDTO;
import com.tuntun.interface_center.user_interface.enums.UserStatus;
import com.tuntun.user_service.entity.Users;
import com.tuntun.user_service.repository.UserRepository;
import com.tuntun.user_service.service.UserService;
import com.tuntun.user_service.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {


    private final UserRepository userRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopicConstants.UPDATE_USER_TOPIC,groupId = "user-group")
    @Transactional
    public void consumeUpdateUser(String message) throws JsonProcessingException {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(ConsumerBaseDTO.class, ConsumerUserUpdateDTO.class);
        ConsumerBaseDTO<ConsumerUserUpdateDTO> consumerBaseDTO = objectMapper.readValue(message,type);
        ConsumerUserUpdateDTO data = consumerBaseDTO.getData();
        log.info("incoming message topic update-user-uid, with message {}",message);
        userRepository.findByPhoneNumber(data.getPhoneNumber()).ifPresentOrElse(users -> {
            Optional.ofNullable(data.getUid()).ifPresent(users::setUid);
            Optional.ofNullable(data.getStatus()).ifPresent(x-> {
                users.setUserStatus(x);
                if(UserStatus.ACTIVE.equals(x)){
                    users.setIsActive(Boolean.TRUE);
                }
            });
            Optional.ofNullable(data.getTransactionPassword()).ifPresent(x-> users.setTransactionPasswordHash(PasswordUtil.hashPassword(x)));
            userRepository.save(users);
        }, () -> log.warn("User with phone number {} is empty, no need to process", data.getPhoneNumber()));
    }



    @KafkaListener(topics = KafkaTopicConstants.CREATE_LOGIN_HISTORY,groupId = "user-group")
    @Transactional
    public void consumeCreateLoginHistory(String message) throws JsonProcessingException {
        log.info("incoming message topic update-user-uid, with message {}",message);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(ConsumerBaseDTO.class, ConsumerLoginHistoryDTO.class);
        ConsumerBaseDTO<ConsumerLoginHistoryDTO> consumerBaseDTO = objectMapper.readValue(message,type);
        ConsumerLoginHistoryDTO data = consumerBaseDTO.getData();

        Optional<Users> usersOptional = Optional.empty();
        if(data.getIsFirstLogin()){
            usersOptional =  userRepository.findByUsername(data.getUsername());
            if(usersOptional.isEmpty()){
                log.warn("user with username {} is not found ",data.getUsername());
                return;
            }
        }else {
             usersOptional =  userRepository.findByUsernameAndIsActiveTrue(data.getUsername());
            if(usersOptional.isEmpty()){
                log.warn("user with username {} is not found ",data.getUsername());
                return;
            }
        }


        Users users = usersOptional.get();

        // create login attempts
        userService.createLoginAttempts(users,data.getDeviceInfo(), data.getIsLoginSuccess());

        // create login history
        if(data.getIsLoginSuccess()){
            userService.createLoginHistory(users,data.getDeviceInfo());

            // create device info
            userService.createDeviceInfo(data.getDeviceInfo(), users);
        }
    }




}
