package com.tutorial.uprit.controller;

import com.tutorial.uprit.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * LikeController — like/unlike posts.
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class LikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, String>> likePost(
            @PathVariable Long postId, @RequestParam Long userId) {
        postLikeService.likePost(userId, postId);
        return new ResponseEntity<>(Map.of("message", "Post liked"), HttpStatus.CREATED);
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId, @RequestParam Long userId) {
        postLikeService.unlikePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<Map<String, Long>> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(Map.of("count", postLikeService.getLikeCount(postId)));
    }
}
