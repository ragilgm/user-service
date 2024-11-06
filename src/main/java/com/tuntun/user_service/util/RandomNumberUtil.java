package com.tuntun.user_service.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;

public class RandomNumberUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generateUniqueIdentifier() {
        // Mengambil tanggal dan waktu dalam format yyyyMMddHHmmss (14 karakter)

        // Menambahkan teks acak untuk mencapai 20 karakter
        String randomPart = generateRandomText("OTP");

        return randomPart;
    }

    private static String hashText(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            // Mengonversi hash byte array ke format hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing text", e);
        }
    }

    public static String generateRandomText(String prefix) {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String dateTimePart = LocalDateTime.now().format(DATE_FORMATTER);

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        if(Objects.nonNull(prefix)){
            sb.append(prefix);
            sb.append("_");
        }

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        sb.append(dateTimePart);

        if(sb.isEmpty())
            return null;

        return hashText(sb.toString());
    }

    public static void main(String[] args) {
        String uniqueIdentifier = generateUniqueIdentifier();
        System.out.println("Unique Identifier: " + uniqueIdentifier);
    }
}
