package com.erms.dto;

import lombok.Data;

@Data
public class ChatPayload {
    private String message;
    private String imageUrl;
    private String type;
}
