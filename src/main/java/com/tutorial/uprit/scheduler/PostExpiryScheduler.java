package com.tutorial.uprit.scheduler;

import com.tutorial.uprit.model.Post;
import com.tutorial.uprit.model.VerificationStatus;
import com.tutorial.uprit.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PostExpiryScheduler — auto-rejects PENDING posts older than 14 days.
 * Runs daily at 2 AM to keep the verification queue clean.
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class PostExpiryScheduler {

    private final PostRepository postRepository;

    private static final int EXPIRY_DAYS = 14;

    @Scheduled(cron = "0 0 2 * * *") // Daily at 2:00 AM
    @Transactional
    public void autoExpirePendingPosts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(EXPIRY_DAYS);
        List<Post> expiredPosts = postRepository.findByVerificationStatusAndCreatedAtBefore(
                VerificationStatus.PENDING, cutoff);

        if (expiredPosts.isEmpty()) {
            log.info("Post expiry: no expired pending posts found.");
            return;
        }

        for (Post post : expiredPosts) {
            post.setVerificationStatus(VerificationStatus.REJECTED);
            post.setVerificationComment("Auto-rejected: not verified within " + EXPIRY_DAYS + " days");
            post.setVerifiedAt(LocalDateTime.now());
        }

        postRepository.saveAll(expiredPosts);
        log.info("Post expiry: auto-rejected {} stale pending posts.", expiredPosts.size());
    }
}
