package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.LeaderboardEntry;
import com.tutorial.uprit.dto.UserResponse;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.User;
import com.tutorial.uprit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * UserService — CRUD + XP/level management + leaderboard.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ── CRUD ───────────────────────────────────────────

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = findUserOrThrow(id);
        return mapToResponse(user);
    }

    public UserResponse updateUser(Long id, UserResponse request) {
        User user = findUserOrThrow(id);
        user.setName(request.getName());
        user.setDepartment(request.getDepartment());
        user.setYear(request.getYear());
        userRepository.save(user);
        return mapToResponse(user);
    }

    public void deleteUser(Long id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    // ── XP & Level ─────────────────────────────────────

    /**
     * Add XP to a user and auto-calculate level.
     * Level formula: level = totalXp / 100
     */
    public UserResponse addXp(Long userId, Integer xpAmount) {
        User user = findUserOrThrow(userId);
        user.setXp(user.getXp() + xpAmount);
        user.setLevel(user.getXp() / 100);  // Auto-calculate level
        userRepository.save(user);
        return mapToResponse(user);
    }

    // ── Leaderboard ────────────────────────────────────

    /**
     * Get leaderboard — users ordered by XP descending.
     * Supports optional limit parameter.
     */
    public List<LeaderboardEntry> getLeaderboard(Integer limit) {
        List<User> users = userRepository.findAllByOrderByXpDesc();

        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardEntry> leaderboard = users.stream()
                .map(user -> LeaderboardEntry.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .department(user.getDepartment())
                        .xp(user.getXp())
                        .level(user.getLevel())
                        .rank(rank.getAndIncrement())
                        .build())
                .collect(Collectors.toList());

        if (limit != null && limit > 0 && limit < leaderboard.size()) {
            return leaderboard.subList(0, limit);
        }
        return leaderboard;
    }

    // ── Helpers ────────────────────────────────────────

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .department(user.getDepartment())
                .year(user.getYear())
                .xp(user.getXp())
                .level(user.getLevel())
                .coins(user.getCoins())
                .role(user.getRole().name())
                .build();
    }
}
