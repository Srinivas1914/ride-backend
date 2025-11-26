package com.blablacar.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phone;

    private String city;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.MEMBER;

    private String vehicleBrand;
    private String vehicleModel;
    private String vehicleColor;
    private String licensePlate;
    private Integer vehicleSeats;

    private Boolean isEmailVerified = false;
    private Boolean isActive = true;
    private Boolean isBlocked = false;

    private String otp;
    private LocalDateTime otpExpiresAt;

    private Double rating = 5.0;
    private Integer totalRatings = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastLoginAt;

    public enum UserRole {
        MEMBER, DRIVER, ADMIN
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}
