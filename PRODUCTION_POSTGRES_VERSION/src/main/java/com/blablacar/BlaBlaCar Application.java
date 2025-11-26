package com.blablacar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlaBlaCar Application {
    public static void main(String[] args) {
        SpringApplication.run(BlaBlaCar Application.class, args);
        System.out.println("\n" + "=".repeat(75));
        System.out.println("✅ APPLICATION STARTED!");
        System.out.println("=".repeat(75));
        System.out.println("🌐 Open: http://localhost:8080");
        System.out.println("🔐 Admin: admin@blablacar.com / Admin@123");
        System.out.println("👤 Driver: driver@demo.com / Demo@123");
        System.out.println("👥 User: user@demo.com / Demo@123");
        System.out.println("=".repeat(75) + "\n");
    }
}
