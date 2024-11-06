package com.tuntun.user_service.controller;

import com.tuntun.interface_center.user_interface.dto.OtpRequestDTO;
import com.tuntun.interface_center.user_interface.dto.OtpResponseDTO;
import com.tuntun.interface_center.user_interface.dto.OtpVerifyRequestDTO;
import com.tuntun.interface_center.user_interface.dto.OtpVerifyResponseDTO;
import com.tuntun.user_service.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otps")
@RequiredArgsConstructor
public class OTPController {

    private final OtpService otpService;

    @PostMapping
    public OtpResponseDTO generateOtp(@RequestBody OtpRequestDTO requestDTO) {
        return otpService.generateOtp(requestDTO);
    }


    @PostMapping("/verify")
    public OtpVerifyResponseDTO verifyOtp(@RequestBody OtpVerifyRequestDTO requestDTO) {
        return otpService.verifyOtp(requestDTO);
    }

}
