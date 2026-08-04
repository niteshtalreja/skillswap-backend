package com.skillswap.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private final Map<String, OTPData> otpStore = new ConcurrentHashMap<>();

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_SECONDS = 300; // 5 minutes

    private static class OTPData {
        String otp;
        long expiryTime;

        OTPData(String otp) {
            this.otp = otp;
            this.expiryTime = System.currentTimeMillis() + (OTP_EXPIRY_SECONDS * 1000);
        }

        boolean isValid() {
            return System.currentTimeMillis() < expiryTime;
        }
    }

    public String generateOTP(String phoneNumber) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(phoneNumber, new OTPData(otp));
        return otp;
    }

    public boolean verifyOTP(String phoneNumber, String otp) {
        OTPData data = otpStore.get(phoneNumber);
        if (data == null || !data.isValid()) {
            return false;
        }
        boolean isValid = data.otp.equals(otp);
        if (isValid) {
            otpStore.remove(phoneNumber);
        }
        return isValid;
    }

    public void deleteOTP(String phoneNumber) {
        otpStore.remove(phoneNumber);
    }
}