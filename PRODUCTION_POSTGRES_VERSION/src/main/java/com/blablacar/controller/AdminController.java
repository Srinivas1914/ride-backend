package com.blablacar.controller;

import com.blablacar.entity.*;
import com.blablacar.service.*;
import com.blablacar.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final RideService rideService;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;

    private User checkAdminAccess(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            throw new RuntimeException("Access denied");
        }
        return user;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User admin = checkAdminAccess(session);

        model.addAttribute("admin", admin);
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalRides", rideRepository.count());
        model.addAttribute("totalBookings", bookingRepository.count());
        model.addAttribute("recentUsers", userRepository.findAll());
        model.addAttribute("recentRides", rideService.getRecentRides());
        model.addAttribute("recentBookings", bookingRepository.findAll());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        checkAdminAccess(session);
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable Long id, HttpSession session) {
        checkAdminAccess(session);
        userService.blockUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        checkAdminAccess(session);
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/rides")
    public String rides(HttpSession session, Model model) {
        checkAdminAccess(session);
        model.addAttribute("rides", rideRepository.findAll());
        return "admin/rides";
    }

    @PostMapping("/rides/{id}/cancel")
    public String cancelRide(@PathVariable Long id, HttpSession session) {
        checkAdminAccess(session);
        rideService.cancelRide(id);
        return "redirect:/admin/rides";
    }
}
