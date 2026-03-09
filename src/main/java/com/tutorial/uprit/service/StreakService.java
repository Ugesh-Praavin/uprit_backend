package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.StreakResponse;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.User;
import com.tutorial.uprit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * StreakService — tracks achievement posting consistency.
 * Called from PostService.createPost().
 */
@Service
@RequiredArgsConstructor
public class StreakService {

    private final UserRepository userRepository;

    /**
     * Update streak when a new achievement is posted.
     * - If last post was yesterday → increment streak
     * - If last post was today → no change
     * - Otherwise → reset to 1
     */
    @Transactional
    public void updateStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastDate = user.getLastAchievementDate();

        // Null-safe: existing DB users have NULL for new columns
        int current = user.getCurrentStreak() != null ? user.getCurrentStreak() : 0;
        int longest = user.getLongestStreak() != null ? user.getLongestStreak() : 0;

        if (lastDate == null) {
            // First ever post
            current = 1;
        } else if (lastDate.equals(today)) {
            // Already posted today — no change
            return;
        } else if (lastDate.equals(today.minusDays(1))) {
            // Posted yesterday — extend streak!
            current = current + 1;
        } else {
            // Streak broken — reset
            current = 1;
        }

        // Update longest streak
        if (current > longest) {
            longest = current;
        }

        user.setCurrentStreak(current);
        user.setLongestStreak(longest);
        user.setLastAchievementDate(today);
        userRepository.save(user);
    }

    public StreakResponse getStreak(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return StreakResponse.builder()
                .currentStreak(user.getCurrentStreak())
                .longestStreak(user.getLongestStreak())
                .lastAchievementDate(user.getLastAchievementDate() != null
                        ? user.getLastAchievementDate().toString()
                        : null)
                .build();
    }
}
