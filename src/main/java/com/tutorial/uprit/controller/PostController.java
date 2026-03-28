package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.PostRequest;
import com.tutorial.uprit.dto.PostResponse;
import com.tutorial.uprit.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PostController — achievement feed APIs.
 * All endpoints require JWT authentication.
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * POST /api/posts?userId={userId} — create an achievement post.
     * XP is calculated automatically by the backend.
     *
     * Request body:
     * {
     * "title": "Won Smart India Hackathon",
     * "description": "Led a team of 6 to build an AI-powered solution",
     * "achievementType": "HACKATHON_WIN",
     * "imageUrl": "https://example.com/trophy.jpg"
     * }
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestParam Long userId,
            @Valid @RequestBody PostRequest request) {
        return new ResponseEntity<>(postService.createPost(userId, request), HttpStatus.CREATED);
    }

    /**
     * GET /api/posts/feed — get the achievement feed (most recent first).
     * Optional: ?limit=20
     */
    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getFeed(
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(postService.getFeed(limit));
    }

    /**
     * GET /api/posts/user/{userId} — get all posts by a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    /**
     * GET /api/posts/my?userId={userId} — get own posts (includes PENDING, VERIFIED, REJECTED).
     * Used in student profile to show post status badges.
     */
    @GetMapping("/my")
    public ResponseEntity<List<PostResponse>> getMyPosts(@RequestParam Long userId) {
        return ResponseEntity.ok(postService.getMyPosts(userId));
    }

    /**
     * DELETE /api/posts/{id} — delete a post.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
