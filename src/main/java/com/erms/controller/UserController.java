package com.erms.controller;

import com.erms.entity.HelpRequest;
import com.erms.enums.EmergencyType;
import com.erms.enums.PriorityLevel;
import com.erms.service.HelpRequestService;
import com.erms.service.NotificationService;
import com.erms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final HelpRequestService requestService;
    private final NotificationService notifService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("myRequests", requestService.getByUser(user.getId()));
        model.addAttribute("unreadCount", notifService.getUnreadCount(user.getId()));
        model.addAttribute("notifications", notifService.getForUser(user.getId()));
        model.addAttribute("emergencyTypes", EmergencyType.values());
        model.addAttribute("priorityLevels", PriorityLevel.values());

        return "user/dashboard";
    }

    @PostMapping("/request/submit")
    public String submitRequest(@RequestParam String fullName,
                                @RequestParam String phoneNumber,
                                @RequestParam EmergencyType emergencyType,
                                @RequestParam PriorityLevel priorityLevel,
                                @RequestParam Integer peopleCount,
                                @RequestParam String address,
                                @RequestParam(required = false) String additionalDetails,
                                @RequestParam(required = false) Double latitude,
                                @RequestParam(required = false) Double longitude,
                                @AuthenticationPrincipal UserDetails userDetails) {

        var user = userService.findByUsername(userDetails.getUsername());

        HelpRequest request = HelpRequest.builder()
                .submittedBy(user)
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .emergencyType(emergencyType)
                .priorityLevel(priorityLevel)
                .peopleCount(peopleCount)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .additionalDetails(additionalDetails)
                .build();

        requestService.submitRequest(request);

        return "redirect:/user/dashboard?success=submitted";
    }
}
