package com.erms.entity;

import com.erms.enums.EmergencyType;
import com.erms.enums.PriorityLevel;
import com.erms.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "volunteer_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private HelpRequest helpRequest;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;

    @Column(name = "task_title")
    private String taskTitle;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "task_status")
    private TaskStatus taskStatus = TaskStatus.ASSIGNED;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "emergency_type")
    private EmergencyType emergencyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level")
    private PriorityLevel priorityLevel;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Builder.Default
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt = LocalDateTime.now();

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
