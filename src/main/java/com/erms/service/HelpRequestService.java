package com.erms.service;

import com.erms.entity.HelpRequest;
import com.erms.entity.User;
import com.erms.entity.Volunteer;
import com.erms.entity.VolunteerTask;
import com.erms.enums.AvailabilityStatus;
import com.erms.enums.RequestStatus;
import com.erms.enums.TaskStatus;
import com.erms.repository.HelpRequestRepository;
import com.erms.repository.VolunteerRepository;
import com.erms.repository.VolunteerTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HelpRequestService {

    private final HelpRequestRepository requestRepo;
    private final VolunteerRepository volunteerRepo;
    private final VolunteerTaskRepository taskRepo;
    private final NotificationService notificationService;

    public HelpRequest submitRequest(HelpRequest request) {
        return requestRepo.save(request);
    }

    public List<HelpRequest> getAllRequests() {
        return requestRepo.findAllByOrderByCreatedAtDesc();
    }

    public List<HelpRequest> getAllActive() {
        return requestRepo.findAllActive();
    }

    public List<HelpRequest> getByUser(Long userId) {
        return requestRepo.findBySubmittedByIdOrderByCreatedAtDesc(userId);
    }

    public long countByStatus(RequestStatus status) {
        return requestRepo.countByStatus(status);
    }

    public void assignVolunteer(Long requestId, Long volunteerId, String notes) {
        HelpRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Volunteer volunteer = volunteerRepo.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));

        request.setAssignedVolunteer(volunteer);
        request.setStatus(RequestStatus.ASSIGNED);
        request.setAdminNotes(notes);
        request.setAssignedAt(LocalDateTime.now());
        requestRepo.save(request);

        volunteer.setAvailabilityStatus(AvailabilityStatus.BUSY);
        volunteerRepo.save(volunteer);

        VolunteerTask task = VolunteerTask.builder()
                .helpRequest(request)
                .volunteer(volunteer)
                .taskTitle(request.getEmergencyType() + " - " + request.getAddress())
                .address(request.getAddress())
                .emergencyType(request.getEmergencyType())
                .priorityLevel(request.getPriorityLevel())
                .contactName(request.getFullName())
                .contactPhone(request.getPhoneNumber())
                .build();

        taskRepo.save(task);

        notificationService.send(
                volunteer.getUser(),
                "New Task Assigned",
                "You have been assigned a new emergency task at " + request.getAddress()
        );

        User requester = request.getSubmittedBy();
        if (requester != null) {
            notificationService.send(
                    requester,
                    "Volunteer Assigned",
                    "A volunteer has been assigned to your emergency request."
            );
        }
    }

    public void updateTaskStatus(Long taskId, TaskStatus status) {
        VolunteerTask task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTaskStatus(status);
        task.setUpdatedAt(LocalDateTime.now());

        if (status == TaskStatus.ACCEPTED) {
            task.setAcceptedAt(LocalDateTime.now());
            task.getHelpRequest().setStatus(RequestStatus.ASSIGNED);
            requestRepo.save(task.getHelpRequest());
        }

        if (status == TaskStatus.IN_PROGRESS) {
            task.getHelpRequest().setStatus(RequestStatus.IN_PROGRESS);
            requestRepo.save(task.getHelpRequest());
        }

        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());

            HelpRequest request = task.getHelpRequest();
            request.setStatus(RequestStatus.COMPLETED);
            request.setCompletedAt(LocalDateTime.now());
            requestRepo.save(request);

            Volunteer volunteer = task.getVolunteer();
            volunteer.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            volunteer.setCompletedTasks(volunteer.getCompletedTasks() + 1);
            volunteerRepo.save(volunteer);

            if (request.getSubmittedBy() != null) {
                notificationService.send(
                        request.getSubmittedBy(),
                        "Request Completed",
                        "Your emergency request has been marked as completed."
                );
            }
        }

        if (status == TaskStatus.REJECTED) {
            HelpRequest request = task.getHelpRequest();
            request.setStatus(RequestStatus.PENDING);
            request.setAssignedVolunteer(null);
            requestRepo.save(request);

            Volunteer volunteer = task.getVolunteer();
            volunteer.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            volunteerRepo.save(volunteer);
        }

        taskRepo.save(task);
    }

    public List<VolunteerTask> getTasksForVolunteer(Long volunteerId, TaskStatus status) {
        return taskRepo.findByVolunteerIdAndTaskStatus(volunteerId, status);
    }

    public List<VolunteerTask> getActiveTasksForVolunteer(Long volunteerId) {
        List<VolunteerTask> tasks = new java.util.ArrayList<>();
        tasks.addAll(getTasksForVolunteer(volunteerId, TaskStatus.ASSIGNED));
        tasks.addAll(getTasksForVolunteer(volunteerId, TaskStatus.ACCEPTED));
        tasks.addAll(getTasksForVolunteer(volunteerId, TaskStatus.IN_PROGRESS));
        return tasks;
    }
}
