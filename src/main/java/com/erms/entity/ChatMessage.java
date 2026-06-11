package com.erms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String room;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "sender_name")
    private String senderName;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "image_url")
    private String imageUrl;

    private String type;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
