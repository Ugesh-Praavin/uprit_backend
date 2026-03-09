package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.LeaderboardEntry;
import com.tutorial.uprit.dto.StreakResponse;
import com.tutorial.uprit.dto.UserProfileResponse;
import com.tutorial.uprit.dto.UserResponse;
import com.tutorial.uprit.dto.XpRequest;
import com.tutorial.uprit.service.ProfileService;
import com.tutorial.uprit.service.StreakService;
import com.tutorial.uprit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController — CRUD + XP + Leaderboard + Profile.
 * All endpoints require JWT authentication.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;
    private final StreakService streakService;

    /** GET /api/users — list all users */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /** GET /api/users/{id} — get user by ID */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /** GET /api/users/profile/{id} — full aggregated profile */
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long id,
            @RequestParam(required = false) Long viewerId) {
        return ResponseEntity.ok(profileService.getUserProfile(id, viewerId));
    }

    /** PUT /api/users/{id} — update user profile */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserResponse request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /** DELETE /api/users/{id} — delete user */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/users/{id}/xp — add XP to a user.
     * Level is auto-calculated as xp / 100.
     */
    @PostMapping("/{id}/xp")
    public ResponseEntity<UserResponse> addXp(
            @PathVariable Long id,
            @Valid @RequestBody XpRequest request) {
        return ResponseEntity.ok(userService.addXp(id, request.getXp()));
    }

    /**
     * GET /api/leaderboard — get leaderboard (ordered by XP desc).
     * Optional query param: ?limit=10
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard(
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(userService.getLeaderboard(limit));
    }

    /** GET /api/users/{id}/streak — get achievement streak */
    @GetMapping("/{id}/streak")
    public ResponseEntity<StreakResponse> getStreak(@PathVariable Long id) {
        return ResponseEntity.ok(streakService.getStreak(id));
    }
}
