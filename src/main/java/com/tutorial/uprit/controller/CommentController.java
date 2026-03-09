package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.CommentRequest;
import com.tutorial.uprit.dto.CommentResponse;
import com.tutorial.uprit.service.PostCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CommentController — add, list (paginated), and delete comments.
 */
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final PostCommentService postCommentService;

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @Valid @RequestBody CommentRequest request) {
        return new ResponseEntity<>(
                postCommentService.addComment(userId, postId, request.getCommentText()),
                HttpStatus.CREATED);
    }

    /** Paginated: GET /api/posts/{postId}/comments?page=0&size=20 */
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(postCommentService.getComments(postId, page, size));
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id, @RequestParam Long userId) {
        postCommentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }
}
