package com.tutorial.uprit.service;

import com.tutorial.uprit.exception.BadRequestException;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PostLikeService — like/unlike with self-like prevention and notification
 * trigger.
 */
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void likePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Prevent self-like
        if (post.getUser().getId().equals(userId)) {
            throw new BadRequestException("Cannot like your own post");
        }

        if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new BadRequestException("Already liked this post");
        }

        PostLike like = PostLike.builder().user(user).post(post).build();
        postLikeRepository.save(like);

        // Trigger notification
        notificationService.createNotification(
                post.getUser().getId(), userId,
                NotificationType.LIKE,
                user.getName() + " liked your achievement post",
                postId);
    }

    @Transactional
    public void unlikePost(Long userId, Long postId) {
        if (!postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new BadRequestException("You haven't liked this post");
        }
        postLikeRepository.deleteByUserIdAndPostId(userId, postId);
    }

    public long getLikeCount(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    public boolean isLikedByUser(Long userId, Long postId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }
}
