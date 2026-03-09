package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.CommentResponse;
import com.tutorial.uprit.exception.BadRequestException;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PostCommentService — add/get/delete with pagination and notification trigger.
 */
@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public CommentResponse addComment(Long userId, Long postId, String commentText) {
        if (commentText == null || commentText.trim().isEmpty()) {
            throw new BadRequestException("Comment cannot be empty");
        }
        if (commentText.length() > 500) {
            throw new BadRequestException("Comment must be under 500 characters");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        PostComment comment = PostComment.builder()
                .post(post)
                .user(user)
                .commentText(commentText.trim())
                .build();

        postCommentRepository.save(comment);

        // Trigger notification (don't notify yourself)
        notificationService.createNotification(
                post.getUser().getId(), userId,
                NotificationType.COMMENT,
                user.getName() + " commented on your post",
                postId);

        return mapToResponse(comment);
    }

    public Page<CommentResponse> getComments(Long postId, int page, int size) {
        return postCommentRepository.findByPostIdOrderByCreatedAtDesc(postId, PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        // Users can only delete their own comments
        if (!comment.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own comments");
        }
        postCommentRepository.delete(comment);
    }

    public long getCommentCount(Long postId) {
        return postCommentRepository.countByPostId(postId);
    }

    private CommentResponse mapToResponse(PostComment c) {
        return CommentResponse.builder()
                .id(c.getId())
                .userId(c.getUser().getId())
                .userName(c.getUser().getName())
                .avatarUrl(c.getUser().getAvatarUrl())
                .commentText(c.getCommentText())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
