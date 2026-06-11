package com.erms.controller;

import com.erms.entity.VolunteerTask;
import com.erms.enums.TaskStatus;
import com.erms.repository.VolunteerRepository;
import com.erms.repository.VolunteerTaskRepository;
import com.erms.service.HelpRequestService;
import com.erms.service.NotificationService;
import com.erms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/volunteer")
@RequiredArgsConstructor
public class VolunteerController {

    private final UserService userService;
    private final VolunteerRepository volunteerRepo;
    private final VolunteerTaskRepository volunteerTaskRepo;
    private final HelpRequestService requestService;
    private final NotificationService notifService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());

        volunteerRepo.findByUserId(user.getId()).ifPresent(volunteer -> {
            model.addAttribute("volunteer", volunteer);
            model.addAttribute("user", user);
            model.addAttribute("assignedTasks",
                    requestService.getTasksForVolunteer(volunteer.getId(), TaskStatus.ASSIGNED));
            model.addAttribute("acceptedTasks",
                    requestService.getTasksForVolunteer(volunteer.getId(), TaskStatus.ACCEPTED));
            model.addAttribute("inProgressTasks",
                    requestService.getTasksForVolunteer(volunteer.getId(), TaskStatus.IN_PROGRESS));
            model.addAttribute("completedTasks",
                    requestService.getTasksForVolunteer(volunteer.getId(), TaskStatus.COMPLETED));
            model.addAttribute("activeTasks", requestService.getActiveTasksForVolunteer(volunteer.getId()));
            model.addAttribute("unreadCount", notifService.getUnreadCount(user.getId()));
            model.addAttribute("notifications", notifService.getForUser(user.getId()));
        });

        return "volunteer/dashboard";
    }

    @PostMapping("/tasks/{id}/update")
    public String updateTask(@PathVariable Long id,
                             @RequestParam TaskStatus status,
                             @AuthenticationPrincipal UserDetails userDetails) {
        var volunteer = volunteerRepo.findByUserUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
        VolunteerTask task = volunteerTaskRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getVolunteer().getId().equals(volunteer.getId())) {
            return "redirect:/volunteer/dashboard?error=unauthorized";
        }

        requestService.updateTaskStatus(id, status);
        return "redirect:/volunteer/dashboard?success=updated";
    }

    @PostMapping("/location/update")
    @ResponseBody
    public ResponseEntity<Void> updateLocation(@RequestBody Map<String, Double> body,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        var volunteer = volunteerRepo.findByUserUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
        volunteer.setCurrentLatitude(body.get("latitude"));
        volunteer.setCurrentLongitude(body.get("longitude"));
        volunteer.setLocationUpdatedAt(LocalDateTime.now());
        volunteerRepo.save(volunteer);
        return ResponseEntity.ok().build();
    }
}
