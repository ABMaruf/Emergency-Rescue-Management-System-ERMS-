package com.erms.controller;

import com.erms.dto.ChatPayload;
import com.erms.entity.ChatMessage;
import com.erms.entity.HelpRequest;
import com.erms.entity.User;
import com.erms.repository.ChatMessageRepository;
import com.erms.repository.HelpRequestRepository;
import com.erms.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final HelpRequestRepository helpRequestRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{room}")
    public void handleChat(@DestinationVariable String room,
                           @Payload ChatPayload payload,
                           Principal principal) {
        HelpRequest request = findRequestByRoom(room);
        if (!request.isChatActive()) {
            return;
        }

        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage message = ChatMessage.builder()
                .room(room)
                .sender(sender)
                .senderName(sender.getFullName())
                .message(payload.getMessage())
                .imageUrl(payload.getImageUrl())
                .type(resolveType(payload.getMessage(), payload.getImageUrl(), payload.getType()))
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);

        messagingTemplate.convertAndSend("/topic/chat/" + room, Map.of(
                "senderUsername", sender.getUsername(),
                "senderName", sender.getFullName(),
                "role", sender.getRole().name(),
                "message", payload.getMessage() == null ? "" : payload.getMessage(),
                "imageUrl", payload.getImageUrl() == null ? "" : payload.getImageUrl(),
                "type", message.getType(),
                "time", message.getCreatedAt().toString()
        ));
    }

    @GetMapping("/chat/{requestId}")
    public String chatPage(@PathVariable Long requestId,
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        String room = "request_" + requestId;
        HelpRequest request = helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        List<ChatMessage> history = chatMessageRepository.findByRoomOrderByCreatedAtAsc(room);

        model.addAttribute("room", room);
        model.addAttribute("requestId", requestId);
        model.addAttribute("request", request);
        model.addAttribute("chatActive", request.isChatActive());
        model.addAttribute("history", history);
        model.addAttribute("currentUser", userDetails.getUsername());
        return "chat/room";
    }

    @PostMapping("/chat/send")
    public String sendMessage(@RequestParam String room,
                              @RequestParam String message,
                              @RequestParam(required = false) String imageUrl,
                              @AuthenticationPrincipal UserDetails userDetails,
                              HttpServletRequest request) {
        HelpRequest helpRequest = findRequestByRoom(room);
        if (!helpRequest.isChatActive()) {
            return redirectBack(request);
        }

        User sender = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage chatMessage = ChatMessage.builder()
                .room(room)
                .sender(sender)
                .senderName(sender.getFullName())
                .message(message)
                .imageUrl(imageUrl)
                .type(resolveType(message, imageUrl, null))
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);
        return redirectBack(request);
    }

    private HelpRequest findRequestByRoom(String room) {
        Long requestId = Long.parseLong(room.replace("request_", ""));
        return helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    private String redirectBack(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    private String resolveType(String message, String imageUrl, String requestedType) {
        if (requestedType != null && !requestedType.isBlank()) {
            return requestedType;
        }

        boolean hasText = message != null && !message.isBlank();
        boolean hasImage = imageUrl != null && !imageUrl.isBlank();

        if (hasText && hasImage) {
            return "BOTH";
        }
        if (hasImage) {
            return "IMAGE";
        }
        return "TEXT";
    }
}
