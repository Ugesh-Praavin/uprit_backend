package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.FollowResponse;
import com.tutorial.uprit.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FollowController — one-way follow system (Instagram-style).
 */
@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /** Follow a user */
    @PostMapping("/{followingId}")
    public ResponseEntity<FollowResponse> follow(
            @RequestParam Long followerId,
            @PathVariable Long followingId) {
        return new ResponseEntity<>(followService.follow(followerId, followingId), HttpStatus.CREATED);
    }

    /** Unfollow a user */
    @DeleteMapping("/{followingId}")
    public ResponseEntity<Void> unfollow(
            @RequestParam Long followerId,
            @PathVariable Long followingId) {
        followService.unfollow(followerId, followingId);
        return ResponseEntity.noContent().build();
    }

    /** Get followers of a user */
    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<FollowResponse>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    /** Get users this user follows */
    @GetMapping("/following/{userId}")
    public ResponseEntity<List<FollowResponse>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }
}
