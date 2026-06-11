package com.erms.service;

import com.erms.entity.Notification;
import com.erms.entity.User;
import com.erms.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;

    public void send(User user, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .build();

        notificationRepo.save(notification);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepo.countByUserIdAndIsReadFalse(userId);
    }

    public List<Notification> getForUser(Long userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markRead(Long notificationId, Long userId) {
        notificationRepo.findById(notificationId).ifPresent(notification -> {
            if (notification.getUser() != null && notification.getUser().getId().equals(userId)) {
                notification.setRead(true);
                notificationRepo.save(notification);
            }
        });
    }

    public void markAllRead(Long userId) {
        List<Notification> unread = notificationRepo.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(notification -> notification.setRead(true));
        notificationRepo.saveAll(unread);
    }
}
