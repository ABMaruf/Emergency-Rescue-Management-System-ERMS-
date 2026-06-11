package com.erms.service;

import com.erms.dto.VolunteerDistanceDTO;
import com.erms.entity.HelpRequest;
import com.erms.entity.Volunteer;
import com.erms.enums.AvailabilityStatus;
import com.erms.repository.HelpRequestRepository;
import com.erms.repository.VolunteerRepository;
import com.erms.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final HelpRequestRepository helpRequestRepository;
    private final VolunteerRepository volunteerRepository;

    public List<VolunteerDistanceDTO> getVolunteersSortedByDistance(Long requestId) {
        HelpRequest request = helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getLatitude() == null || request.getLongitude() == null) {
            return List.of();
        }

        return volunteerRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE).stream()
                .filter(v -> v.getCurrentLatitude() != null && v.getCurrentLongitude() != null)
                .map(v -> toDistanceDto(request, v))
                .sorted(Comparator.comparingDouble(VolunteerDistanceDTO::getDistanceKm))
                .toList();
    }

    private VolunteerDistanceDTO toDistanceDto(HelpRequest request, Volunteer volunteer) {
        double distanceKm = GeoUtils.haversine(
                request.getLatitude(),
                request.getLongitude(),
                volunteer.getCurrentLatitude(),
                volunteer.getCurrentLongitude()
        );
        return new VolunteerDistanceDTO(volunteer, distanceKm, GeoUtils.formatDistance(distanceKm));
    }
}
