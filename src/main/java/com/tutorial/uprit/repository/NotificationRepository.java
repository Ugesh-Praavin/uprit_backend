package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.Notification;
import com.tutorial.uprit.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);

    /** Mark all notifications as read for a user */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.id = :userId AND n.read = false")
    void markAllAsRead(@Param("userId") Long userId);

    /** Check for duplicate notification (prevents spam from rapid like/unlike) */
    boolean existsByUserIdAndActorIdAndTypeAndReferenceId(
            Long userId, Long actorId, NotificationType type, Long referenceId);
}
