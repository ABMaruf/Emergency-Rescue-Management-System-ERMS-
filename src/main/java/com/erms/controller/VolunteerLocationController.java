package com.erms.controller;

import com.erms.dto.LocationPayload;
import com.erms.entity.User;
import com.erms.entity.Volunteer;
import com.erms.repository.UserRepository;
import com.erms.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/volunteer")
@RequiredArgsConstructor
public class VolunteerLocationController {

    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;

    @PostMapping("/location")
    public ResponseEntity<Void> updateLocation(@RequestBody LocationPayload payload,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Volunteer volunteer = volunteerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));

        volunteer.setCurrentLatitude(payload.getLat());
        volunteer.setCurrentLongitude(payload.getLng());
        volunteer.setLocationUpdatedAt(LocalDateTime.now());
        volunteerRepository.save(volunteer);

        return ResponseEntity.ok().build();
    }
}
