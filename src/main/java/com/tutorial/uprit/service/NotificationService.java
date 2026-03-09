package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.NotificationResponse;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.NotificationRepository;
import com.tutorial.uprit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Create a notification — prevents duplicates (same actor + type + reference).
     * Skips if receiver == actor (no self-notifications).
     */
    public void createNotification(Long receiverId, Long actorId, NotificationType type,
            String message, Long referenceId) {
        // Don't notify yourself
        if (receiverId.equals(actorId))
            return;

        // Prevent duplicate notifications
        if (notificationRepository.existsByUserIdAndActorIdAndTypeAndReferenceId(
                receiverId, actorId, type, referenceId)) {
            return;
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", receiverId));
        User actor = actorId != null ? userRepository.findById(actorId).orElse(null) : null;

        Notification notification = Notification.builder()
                .user(receiver)
                .actor(actor)
                .type(type)
                .message(message)
                .referenceId(referenceId)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType().name())
                .message(n.getMessage())
                .referenceId(n.getReferenceId())
                .read(n.getRead())
                .createdAt(n.getCreatedAt())
                .actorId(n.getActor() != null ? n.getActor().getId() : null)
                .actorName(n.getActor() != null ? n.getActor().getName() : null)
                .actorAvatarUrl(n.getActor() != null ? n.getActor().getAvatarUrl() : null)
                .build();
    }
}
