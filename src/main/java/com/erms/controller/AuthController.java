package com.erms.controller;

import com.erms.enums.UserRole;
import com.erms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("roles", new UserRole[]{UserRole.USER, UserRole.VOLUNTEER});
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String fullName,
                           @RequestParam(required = false) String phone,
                           @RequestParam UserRole role,
                           @RequestParam(required = false) String teamName,
                           @RequestParam(required = false) String specialization,
                           @RequestParam(required = false) Double currentLatitude,
                           @RequestParam(required = false) Double currentLongitude,
                           Model model) {
        try {
            userService.registerUser(
                    username,
                    email,
                    password,
                    fullName,
                    phone,
                    role,
                    teamName,
                    specialization,
                    currentLatitude,
                    currentLongitude
            );
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", new UserRole[]{UserRole.USER, UserRole.VOLUNTEER});
            return "auth/register";
        }
    }
}
