package com.tuntun.user_service.util;

import java.util.Random;

public class OtpUtil {

    public static String generateOtpCode() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // Generate 6-digit OTP
        return String.valueOf(otp);
    }

}
