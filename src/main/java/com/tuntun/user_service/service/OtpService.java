package com.tuntun.user_service.service;


import com.tuntun.interface_center.user_interface.dto.OtpRequestDTO;
import com.tuntun.interface_center.user_interface.dto.OtpResponseDTO;
import com.tuntun.interface_center.user_interface.dto.OtpVerifyRequestDTO;
import com.tuntun.interface_center.user_interface.dto.OtpVerifyResponseDTO;
import com.tuntun.interface_center.user_interface.enums.OtpStatus;
import com.tuntun.user_service.entity.Otp;
import com.tuntun.user_service.repository.OTPRepository;
import com.tuntun.user_service.util.OtpUtil;
import com.tuntun.user_service.util.RandomNumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OTPRepository otpRepository;

    public OtpResponseDTO generateOtp(OtpRequestDTO requestDTO) {

        String uniqueIdentifier = RandomNumberUtil.generateRandomText("OTP");

        CompletableFuture.supplyAsync(()-> {
            Optional<Otp> otpOptional = otpRepository.findByPhoneNumberAndActionTypeAndOtpStatus(requestDTO.getPhoneNumber(),requestDTO.getActionType(),OtpStatus.PENDING);
            if(otpOptional.isPresent()){
                Otp otp = otpOptional.get();
                // check otp expired and isUsed
                if(otp.getExpiresAt().isBefore(LocalDateTime.now())){
                    log.warn("request otp {} actionType {} is rejected , found otp active",requestDTO.getPhoneNumber(),requestDTO.getActionType());
                    otp.setOtpStatus(OtpStatus.OTP_EXPIRED);
                    otpRepository.save(otp);
                }
                OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
                otpResponseDTO.setIsSuccess(Boolean.FALSE);
                return otpResponseDTO;
            }
            int OTP_EXPIRATION_MINUTES = 3;
            Otp otp = new Otp();
            otp.setUniqueIdentifier(uniqueIdentifier);
            otp.setPhoneNumber(requestDTO.getPhoneNumber());
            otp.setActionType(requestDTO.getActionType());
            otp.setOtpCode(OtpUtil.generateOtpCode());
            otp.setOtpStatus(OtpStatus.PENDING);
            otp.setCreatedAt(LocalDateTime.now());
            otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
            otp.setUsed(false);
            otpRepository.saveAndFlush(otp);
            return Boolean.TRUE;
        });

        OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
        otpResponseDTO.setIsSuccess(Boolean.TRUE);
        otpResponseDTO.setUniqueIdentifier(uniqueIdentifier);
        return otpResponseDTO;
    }


    public OtpVerifyResponseDTO verifyOtp(OtpVerifyRequestDTO requestDTO) {

        OtpVerifyResponseDTO response  = new OtpVerifyResponseDTO();

        Optional<Otp> otpOptional = otpRepository.findByPhoneNumberAndUniqueIdentifierAndActionType(requestDTO.getPhoneNumber(),requestDTO.getUniqueIdentifier(),requestDTO.getActionType());
        if(otpOptional.isEmpty()){
            log.error("Otp invalid or identifier not found , param {}",requestDTO.getUniqueIdentifier());
            response.setStatus(OtpStatus.IDENTIFIER_INVALID);
            return response;
        }

        Otp otp = otpOptional.get();

        boolean isValid = Objects.equals(otp.getOtpCode(),requestDTO.getOtpCode());
        if(!isValid){
            log.warn("Otp invalid");
            response.setStatus(OtpStatus.OTP_INVALID);
            return response;
        }

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("otp with id {} has been expired",otp.getId());
            otp.setOtpStatus(OtpStatus.OTP_EXPIRED);
            otpRepository.saveAndFlush(otp);
            response.setStatus(OtpStatus.OTP_EXPIRED);
            return response;
        }

        if(otp.getUsed()){
            log.warn("otp with id {} has been used",otp.getId());
            response.setStatus(OtpStatus.OTP_HAS_BEEN_USED);
            return response;
        }
        otp.setOtpStatus(OtpStatus.OTP_VALID);
        otpRepository.saveAndFlush(otp);

        response.setStatus(OtpStatus.OTP_VALID);
        return response;
    }

}
