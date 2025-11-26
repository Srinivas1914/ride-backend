package com.blablacar.controller;

import com.blablacar.entity.*;
import com.blablacar.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;
    private final RideService rideService;
    private final BookingService bookingService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recentRides", rideService.getRecentRides());
        return "index";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        try {
            User registered = userService.registerUser(user);
            model.addAttribute("userId", registered.getId());
            model.addAttribute("email", registered.getEmail());
            return "verify-email";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam Long userId, @RequestParam String otp, Model model) {
        if (userService.verifyEmail(userId, otp)) {
            return "login";
        }
        model.addAttribute("error", "Invalid OTP");
        model.addAttribute("userId", userId);
        return "verify-email";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String emailOrPhone, @RequestParam String password, 
                       HttpSession session, Model model) {
        Map<String, Object> response = userService.initiateLogin(emailOrPhone, password);

        if ((Boolean) response.get("success")) {
            if ((Boolean) response.getOrDefault("isAdmin", false)) {
                User admin = (User) response.get("user");
                session.setAttribute("user", admin);
                return "redirect:/admin/dashboard";
            }
            model.addAttribute("userId", response.get("userId"));
            return "verify-login";
        }

        model.addAttribute("error", response.get("message"));
        return "login";
    }

    @PostMapping("/verify-login")
    public String verifyLogin(@RequestParam Long userId, @RequestParam String otp, 
                             HttpSession session, Model model) {
        try {
            User user = userService.verifyLogin(userId, otp);
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", userId);
            return "verify-login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if (user.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("user", user);
        model.addAttribute("myBookings", bookingService.getPassengerBookings(user));

        if (user.getRole() == User.UserRole.DRIVER) {
            model.addAttribute("myRides", rideService.getDriverRides(user));
            model.addAttribute("driverBookings", bookingService.getDriverBookings(user));
        }

        return "dashboard";
    }

    @GetMapping("/search")
    public String searchPage(@RequestParam(required = false) String from,
                            @RequestParam(required = false) String to,
                            Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        if (from != null && to != null) {
            model.addAttribute("rides", rideService.searchRides(from, to, LocalDate.now(), 1));
        } else {
            model.addAttribute("rides", rideService.getRecentRides());
        }

        return "search";
    }

    @GetMapping("/ride/{id}")
    public String rideDetails(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        Ride ride = rideService.getRideById(id);
        rideService.incrementViews(id);

        model.addAttribute("ride", ride);
        model.addAttribute("user", session.getAttribute("user"));

        return "ride-details";
    }

    @PostMapping("/book-ride/{rideId}")
    public String bookRide(@PathVariable Long rideId,
                          @RequestParam Integer seats,
                          HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        try {
            bookingService.createBooking(rideId, user, seats, "");
            return "redirect:/dashboard?booked=true";
        } catch (Exception e) {
            return "redirect:/ride/" + rideId + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
