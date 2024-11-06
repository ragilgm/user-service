package com.tuntun.user_service.service;


import com.tuntun.datasource_starter.enums.SearchOperation;
import com.tuntun.datasource_starter.service.GenericSpecificationService;
import com.tuntun.datasource_starter.service.model.SearchCriteria;
import com.tuntun.datasource_starter.utils.EncryptionUtils;
import com.tuntun.interface_center.user_interface.dto.*;
import com.tuntun.interface_center.user_interface.enums.LoginStatus;
import com.tuntun.interface_center.user_interface.enums.OTPActionType;
import com.tuntun.interface_center.user_interface.enums.OtpStatus;
import com.tuntun.interface_center.user_interface.enums.UserStatus;
import com.tuntun.user_service.entity.*;
import com.tuntun.user_service.repository.*;
import com.tuntun.user_service.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final DeviceInfoRepository deviceInfoRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final LoginAttemptsRepository loginAttemptsRepository;
    private final OTPRepository otpRepository;
    private final TransactionTemplate transactionTemplate;
    public Page<UserResponseDTO> getUser(UserRequestDTO requestDTO, Pageable pageRequest){
        GenericSpecificationService<Users> spec = new GenericSpecificationService<>();
//        // Add search criteria
        spec.add(new SearchCriteria<>("id", requestDTO.getId(), SearchOperation.EQUAL));
        spec.add(new SearchCriteria<>("phoneNumber", EncryptionUtils.encrypt(requestDTO.getPhoneNumber()), SearchOperation.EQUAL));
        spec.add(new SearchCriteria<>("username", EncryptionUtils.encrypt(requestDTO.getUsername()), SearchOperation.EQUAL));
        spec.add(new SearchCriteria<>("email", EncryptionUtils.encrypt(requestDTO.getEmail()), SearchOperation.EQUAL));

        Page<Users> districtAreaPage = userRepository.findAll(spec,pageRequest);
        return districtAreaPage.map(data->{
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            BeanUtils.copyProperties(data, userResponseDTO);
            return userResponseDTO;
        });
    }

    public CreateUserResponseDTO createUser(CreateUserRequestDTO requestDTO) throws Exception {
        boolean isPresentPhoneNumber = userRepository.findByPhoneNumberAndIsActiveTrue(requestDTO.getPhoneNumber()).isPresent();
        CreateUserResponseDTO response = new CreateUserResponseDTO();
        // if username or phoneNumber already exist then throw
        if(isPresentPhoneNumber){
            log.error("user already exist");
            response.setStatus(Boolean.FALSE);
            response.setRemark("User already exist");
            return response;
        }
        // validate unique identifier


        boolean isPresentUsername = userRepository.findByUsernameAndIsActiveTrue(requestDTO.getUsername()).isPresent();
        // if username or phoneNumber already exist then throw
        if(isPresentUsername){
            log.warn("username already exist");
            response.setStatus(Boolean.FALSE);
            response.setRemark("username already exist");
            return response;
        }

        Optional<Otp> otpOptional =  otpRepository.findByPhoneNumberAndUniqueIdentifierAndActionType(requestDTO.getPhoneNumber(),requestDTO.getUniqueIdentifier(), OTPActionType.REGISTER);
        if(otpOptional.isEmpty()){
            log.warn("register unique identifier is not found {}",requestDTO.getUniqueIdentifier());
            response.setStatus(Boolean.FALSE);
            response.setRemark("Unique identifier not found");
            return response;
        }

        Otp otp = otpOptional.get();
        if(!OtpStatus.OTP_VALID.equals(otp.getOtpStatus())){
            log.warn("register otp is invalid, otp id {}",otp.getId());
            response.setStatus(Boolean.FALSE);
            response.setRemark("Otp invalid");
            return response;
        }

        if(otp.getUsed()){
            log.warn("unique identifier already used {}",otp.getId());
            response.setStatus(Boolean.FALSE);
            response.setRemark("Unique identifier already userd");
            return response;
        }
        // init user
       Boolean success =  transactionTemplate.execute(status -> {
            try {
                createNewUser(requestDTO);

                otp.setUsed(Boolean.TRUE);
                otpRepository.saveAndFlush(otp);

                return Boolean.TRUE; // Return success status
            } catch (Exception e) {
                status.setRollbackOnly(); // Rollback transaction if any exception occurs
                // Log the error or handle it as needed
                log.error("error while initialize data, detail ",e);
                return Boolean.FALSE; // Return failure status
            }
        });

        response.setStatus(success);
        return response;
    }

    public void createDeviceInfo(DeviceInfoRequestDTO requestDTO, Users users) {

       List<DeviceInfo> deviceInfos =  deviceInfoRepository.findByUserId(users.getId());
       DeviceInfo existingDeviceInfo = deviceInfos.stream().filter(data-> Objects.equals(data.getDeviceIdentifier(),requestDTO.getDeviceIdentifier()))
               .findFirst().orElse(null);

       if(Objects.nonNull(existingDeviceInfo)){
           return;
       }

        DeviceInfo deviceInfo = new DeviceInfo();
        // Map DeviceInfoRequestDTO to DeviceInfo
        BeanUtils.copyProperties(requestDTO, deviceInfo);
        deviceInfo.setUser(users); // Set the user reference in DeviceInfo

        // Save device info
        deviceInfoRepository.saveAndFlush(deviceInfo);
    }

    public Users getUserByPhoneNumber(String phoneNumber){
        Optional<Users> usersOptional = userRepository.findByPhoneNumberAndIsActiveTrue(phoneNumber);
        return usersOptional.orElse(null);
    }

    private void createNewUser(CreateUserRequestDTO requestDTO) {
        Users users = new Users();
        // Create user
        LocalDateTime now = LocalDateTime.now();
        users.setPhoneNumber(requestDTO.getPhoneNumber());
        users.setUid(requestDTO.getUid());
        users.setUsername(requestDTO.getUsername());
        users.setPasswordHash(PasswordUtil.hashPassword(requestDTO.getPassword())); // Hash password
        users.setEmail(requestDTO.getEmail());
        users.setCreatedAt(now);
        users.setUserStatus(UserStatus.PENDING);
        users.setUpdatedAt(now);
        users.setIsActive(Boolean.FALSE); // Set the user as active
         userRepository.saveAndFlush(users);
    }

    public LoginUserResponseDTO loginUser(LoginUserRequestDTO requestDTO) throws Exception {
        LoginUserResponseDTO loginUserResponseDTO = new LoginUserResponseDTO();
       Optional<Users> usersOptional =  userRepository.findByUsernameAndIsActiveTrue(requestDTO.getUsername());
       if(usersOptional.isEmpty()){
           throw new Exception("user empty");
       }

       Users users = usersOptional.get();
        boolean isValid = PasswordUtil.verifyPassword(requestDTO.getPassword(),users.getPasswordHash());
        if(isValid){
            // check device info
           List<DeviceInfo> deviceInfoOptional =  deviceInfoRepository.findByUserId(users.getId());
            DeviceInfo deviceInfo = deviceInfoOptional.stream().filter(data-> Objects.equals(data.getDeviceIdentifier(),requestDTO.getDeviceInfo().getDeviceIdentifier()))
                    .findFirst().orElse(null);

            if(Objects.isNull(deviceInfo)){
                log.warn("user is need verify otp");
                loginUserResponseDTO.setStatus(LoginStatus.NEED_OTP);
                return loginUserResponseDTO;
            }

            // no need verify otp
            loginUserResponseDTO.setStatus(LoginStatus.VALID);
            return loginUserResponseDTO;

        }else{
            log.warn("user invalid login");
            // no need verify otp
            loginUserResponseDTO.setStatus(LoginStatus.INVALID);
            return loginUserResponseDTO;
        }

    }

    public void createLoginHistory(Users users,DeviceInfoRequestDTO requestDTO) {
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUser(users);
        loginHistory.setLatitude(requestDTO.getLatitude());
        loginHistory.setLongitude(requestDTO.getLongitude());
        loginHistory.setDevice(requestDTO.getDeviceName());
        loginHistoryRepository.saveAndFlush(loginHistory);
    }

    public void createLoginAttempts(Users users,DeviceInfoRequestDTO deviceInfoRequestDTO, boolean isValid) {
        LoginAttempts loginAttempts = new LoginAttempts();
        loginAttempts.setUser(users);
        loginAttempts.setDevice(deviceInfoRequestDTO.getDeviceName());
        loginAttempts.setLatitude(deviceInfoRequestDTO.getLatitude());
        loginAttempts.setLongitude(deviceInfoRequestDTO.getLongitude());
        loginAttempts.setSuccess(isValid);
        loginAttempts.setDeviceIdentifier(deviceInfoRequestDTO.getDeviceIdentifier());
        loginAttemptsRepository.saveAndFlush(loginAttempts);

    }
}
