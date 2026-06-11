package com.erms.dto;

import com.erms.entity.Volunteer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VolunteerDistanceDTO {
    private Volunteer volunteer;
    private double distanceKm;
    private String distanceFormatted;
}
