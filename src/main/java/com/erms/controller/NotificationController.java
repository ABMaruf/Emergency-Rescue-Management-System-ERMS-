package com.erms.controller;

import com.erms.enums.UserRole;
import com.erms.service.NotificationService;
import com.erms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("notifications", notificationService.getForUser(user.getId()));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user.getId()));
        model.addAttribute("dashboardUrl", dashboardUrl(user.getRole()));
        model.addAttribute("roleLabel", user.getRole().name());
        return "notifications/index";
    }

    @PostMapping("/{id}/read")
    public String markRead(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());
        notificationService.markRead(id, user.getId());
        return "redirect:/notifications";
    }

    @PostMapping("/read-all")
    public String markAllRead(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());
        notificationService.markAllRead(user.getId());
        return "redirect:/notifications";
    }

    private String dashboardUrl(UserRole role) {
        if (role == UserRole.ADMIN) {
            return "/admin/dashboard";
        }
        if (role == UserRole.VOLUNTEER) {
            return "/volunteer/dashboard";
        }
        return "/user/dashboard";
    }
}
