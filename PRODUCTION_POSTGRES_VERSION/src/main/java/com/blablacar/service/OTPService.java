package com.blablacar.service;

import com.blablacar.entity.User;
import com.blablacar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OTPService {

    private final UserRepository userRepository;
    private final SecureRandom random = new SecureRandom();

    public String generateAndSendOTP(User user) {
        String otp = String.format("%06d", random.nextInt(1000000));
        user.setOtp(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        System.out.println("\n" + "=".repeat(70));
        System.out.println("📧 OTP for " + user.getEmail() + ": " + otp);
        System.out.println("⏰ Valid for 10 minutes");
        System.out.println("=".repeat(70) + "\n");

        return otp;
    }

    public boolean verifyOTP(User user, String otp) {
        if (user.getOtp() == null || user.getOtpExpiresAt() == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(user.getOtpExpiresAt())) {
            return false;
        }
        return user.getOtp().equals(otp);
    }

    public void clearOTP(User user) {
        user.setOtp(null);
        user.setOtpExpiresAt(null);
        userRepository.save(user);
    }
}
