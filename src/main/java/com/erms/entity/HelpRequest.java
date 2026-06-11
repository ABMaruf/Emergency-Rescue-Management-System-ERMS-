package com.erms.entity;

import com.erms.enums.EmergencyType;
import com.erms.enums.PriorityLevel;
import com.erms.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "help_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HelpRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "submitted_by")
    private User submittedBy;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "emergency_type", nullable = false)
    private EmergencyType emergencyType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "priority_level")
    private PriorityLevel priorityLevel = PriorityLevel.MEDIUM;

    @Column(name = "people_count", nullable = false)
    private Integer peopleCount;

    @Column(nullable = false)
    private String address;

    private Double latitude;

    private Double longitude;

    @Column(name = "additional_details", columnDefinition = "TEXT")
    private String additionalDetails;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "assigned_volunteer_id")
    private Volunteer assignedVolunteer;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public boolean isChatActive() {
        return status == RequestStatus.PENDING
                || status == RequestStatus.ASSIGNED
                || status == RequestStatus.IN_PROGRESS;
    }
}
