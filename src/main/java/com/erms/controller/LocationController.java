package com.erms.controller;

import com.erms.dto.LocationPayload;
import com.erms.repository.HelpRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class LocationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final HelpRequestRepository helpRequestRepository;

    @MessageMapping("/location/volunteer/{requestId}")
    public void volunteerLocation(@DestinationVariable Long requestId,
                                  @Payload LocationPayload payload,
                                  Principal principal) {
        payload.setType("VOLUNTEER");
        payload.setSenderName(principal.getName());
        messagingTemplate.convertAndSend("/topic/location/" + requestId, payload);
    }

    @MessageMapping("/location/user/{requestId}")
    public void userLocation(@DestinationVariable Long requestId,
                             @Payload LocationPayload payload,
                             Principal principal) {
        payload.setType("USER");
        payload.setSenderName(principal.getName());
        messagingTemplate.convertAndSend("/topic/location/" + requestId, payload);
    }

    @GetMapping("/map/{requestId}")
    public String mapPage(@PathVariable Long requestId, Model model) {
        var request = helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        model.addAttribute("requestId", requestId);
        model.addAttribute("request", request);
        model.addAttribute("volunteer", request.getAssignedVolunteer());
        return "map/live";
    }
}
