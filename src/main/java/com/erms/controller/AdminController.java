package com.erms.controller;

import com.erms.enums.AvailabilityStatus;
import com.erms.enums.RequestStatus;
import com.erms.enums.UserRole;
import com.erms.entity.HelpRequest;
import com.erms.repository.UserRepository;
import com.erms.repository.VolunteerRepository;
import com.erms.service.AdminService;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final HelpRequestService requestService;
    private final VolunteerRepository volunteerRepo;
    private final UserRepository userRepo;
    private final UserService userService;
    private final NotificationService notifService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("totalRequests", requestService.getAllRequests().size());
        model.addAttribute("pendingCount", requestService.countByStatus(RequestStatus.PENDING));
        model.addAttribute("activeCount", requestService.countByStatus(RequestStatus.IN_PROGRESS));
        model.addAttribute("completedCount", requestService.countByStatus(RequestStatus.COMPLETED));
        model.addAttribute("totalVolunteers", volunteerRepo.count());
        model.addAttribute("availableVolunteers",
                volunteerRepo.countByAvailabilityStatus(AvailabilityStatus.AVAILABLE));
        model.addAttribute("totalUsers", userRepo.countActiveByRole(UserRole.USER));
        model.addAttribute("recentRequests", requestService.getAllActive());

        userRepo.findByUsername(userDetails.getUsername()).ifPresent(user -> {
            model.addAttribute("adminName", user.getFullName());
            model.addAttribute("unreadCount", notifService.getUnreadCount(user.getId()));
            model.addAttribute("notifications", notifService.getForUser(user.getId()));
        });

        return "admin/dashboard";
    }

    @GetMapping("/requests")
    public String requests(Model model) {
        var requests = requestService.getAllRequests();
        var nearestMap = new java.util.HashMap<Long, java.util.List<com.erms.dto.VolunteerDistanceDTO>>();
        for (HelpRequest request : requests) {
            if (request.getLatitude() != null && request.getLongitude() != null) {
                nearestMap.put(request.getId(), adminService.getVolunteersSortedByDistance(request.getId()));
            }
        }

        model.addAttribute("allRequests", requests);
        model.addAttribute("availableVolunteers", volunteerRepo.findAvailableOrderByRating());
        model.addAttribute("allVolunteers", volunteerRepo.findAvailableOrderByRating());
        model.addAttribute("nearestMap", nearestMap);
        return "admin/requests";
    }

    @PostMapping("/requests/{id}/assign")
    public String assignVolunteer(@PathVariable Long id,
                                  @RequestParam Long volunteerId,
                                  @RequestParam(required = false) String notes) {
        requestService.assignVolunteer(id, volunteerId, notes);
        return "redirect:/admin/requests?success=assigned";
    }

    @GetMapping("/volunteers")
    public String volunteers(Model model) {
        model.addAttribute("volunteers", volunteerRepo.findAll());
        return "admin/volunteers";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return "redirect:/admin/users";
    }
}
