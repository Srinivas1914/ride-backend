package com.blablacar.service;

import com.blablacar.entity.User;
import com.blablacar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OTPService otpService;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        // Create admin if not exists
        if (!userRepository.existsByEmail("admin@blablacar.com")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@blablacar.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setPhone("+919999999999");
            admin.setRole(User.UserRole.ADMIN);
            admin.setIsEmailVerified(true);
            admin.setCity("System");
            userRepository.save(admin);
            System.out.println("\n" + "=".repeat(70));
            System.out.println("🔐 ADMIN CREATED: admin@blablacar.com / Admin@123");
            System.out.println("=".repeat(70) + "\n");
        }

        // Create demo driver if not exists
        if (!userRepository.existsByEmail("driver@demo.com")) {
            User driver = new User();
            driver.setFirstName("Demo");
            driver.setLastName("Driver");
            driver.setEmail("driver@demo.com");
            driver.setPassword(passwordEncoder.encode("Demo@123"));
            driver.setPhone("+919876543210");
            driver.setRole(User.UserRole.DRIVER);
            driver.setIsEmailVerified(true);
            driver.setCity("Hyderabad");
            driver.setVehicleBrand("Toyota");
            driver.setVehicleModel("Fortuner");
            driver.setVehicleColor("Silver");
            driver.setLicensePlate("TS09AB1234");
            driver.setVehicleSeats(4);
            driver.setRating(4.8);
            driver.setTotalRatings(25);
            userRepository.save(driver);
            System.out.println("✅ DEMO DRIVER CREATED: driver@demo.com / Demo@123");
        }

        // Create demo passenger if not exists
        if (!userRepository.existsByEmail("user@demo.com")) {
            User passenger = new User();
            passenger.setFirstName("Demo");
            passenger.setLastName("Passenger");
            passenger.setEmail("user@demo.com");
            passenger.setPassword(passwordEncoder.encode("Demo@123"));
            passenger.setPhone("+919111222333");
            passenger.setRole(User.UserRole.MEMBER);
            passenger.setIsEmailVerified(true);
            passenger.setCity("Bangalore");
            passenger.setRating(4.5);
            passenger.setTotalRatings(12);
            userRepository.save(passenger);
            System.out.println("✅ DEMO PASSENGER CREATED: user@demo.com / Demo@123\n");
        }
    }

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        otpService.generateAndSendOTP(savedUser);
        return savedUser;
    }

    @Transactional
    public boolean verifyEmail(Long userId, String otp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (otpService.verifyOTP(user, otp)) {
            user.setIsEmailVerified(true);
            otpService.clearOTP(user);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public Map<String, Object> initiateLogin(String emailOrPhone, String password) {
        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findByEmailOrPhone(emailOrPhone, emailOrPhone).orElse(null);

        if (user == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return response;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("success", false);
            response.put("message", "Invalid password");
            return response;
        }

        if (!user.getIsEmailVerified() && !user.isAdmin()) {
            response.put("success", false);
            response.put("message", "Please verify your email");
            return response;
        }

        if (user.getIsBlocked()) {
            response.put("success", false);
            response.put("message", "Account blocked");
            return response;
        }

        // Admin login without OTP
        if (user.isAdmin()) {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            response.put("success", true);
            response.put("isAdmin", true);
            response.put("user", user);
            return response;
        }

        otpService.generateAndSendOTP(user);
        response.put("success", true);
        response.put("isAdmin", false);
        response.put("userId", user.getId());
        return response;
    }

    @Transactional
    public User verifyLogin(Long userId, String otp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (otpService.verifyOTP(user, otp)) {
            otpService.clearOTP(user);
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            return user;
        }

        throw new RuntimeException("Invalid OTP");
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateProfile(Long userId, User updatedData) {
        User user = getUserById(userId);
        user.setFirstName(updatedData.getFirstName());
        user.setLastName(updatedData.getLastName());
        user.setCity(updatedData.getCity());
        return userRepository.save(user);
    }

    @Transactional
    public void updateVehicleInfo(Long userId, String brand, String model, String color, String plate, Integer seats) {
        User user = getUserById(userId);
        user.setVehicleBrand(brand);
        user.setVehicleModel(model);
        user.setVehicleColor(color);
        user.setLicensePlate(plate);
        user.setVehicleSeats(seats);
        user.setRole(User.UserRole.DRIVER);
        userRepository.save(user);
    }

    @Transactional
    public void blockUser(Long userId) {
        User user = getUserById(userId);
        user.setIsBlocked(true);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
