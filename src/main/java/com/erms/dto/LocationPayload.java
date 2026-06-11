package com.erms.dto;

import lombok.Data;

@Data
public class LocationPayload {
    private String type;
    private double lat;
    private double lng;
    private String senderName;
}
