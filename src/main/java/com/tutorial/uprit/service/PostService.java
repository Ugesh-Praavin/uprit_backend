package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.PostRequest;
import com.tutorial.uprit.dto.PostResponse;
import com.tutorial.uprit.exception.BadRequestException;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.PostCommentRepository;
import com.tutorial.uprit.repository.PostLikeRepository;
import com.tutorial.uprit.repository.PostRepository;
import com.tutorial.uprit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PostService — handles achievement feed, verification, streak, and engagement.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final StreakService streakService;
    private final NotificationService notificationService;

    // ═══════════════════════════════════════════════════════════════
    // XP RULES ENGINE
    // ═══════════════════════════════════════════════════════════════

    private int calculateXp(AchievementType type) {
        return switch (type) {
            case HACKATHON_WIN -> 100;
            case HACKATHON_PARTICIPATION -> 50;
            case CERTIFICATE -> 30;
            case PROJECT_COMPLETION -> 70;
            case INTERNSHIP -> 120;
            case RESEARCH_PAPER -> 150;
            case OTHER -> 10;
        };
    }

    private int calculateVerificationBonus(AchievementType type) {
        return calculateXp(type) / 2;
    }

    // ═══════════════════════════════════════════════════════════════
    // CREATE POST (+ Streak Update)
    // ═══════════════════════════════════════════════════════════════

    @Transactional
    public PostResponse createPost(Long userId, PostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        AchievementType type;
        try {
            type = AchievementType.valueOf(request.getAchievementType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid achievement type: " + request.getAchievementType()
                            + ". Valid: HACKATHON_WIN, HACKATHON_PARTICIPATION, CERTIFICATE, "
                            + "PROJECT_COMPLETION, INTERNSHIP, RESEARCH_PAPER, OTHER");
        }

        int xp = calculateXp(type);

        Post post = Post.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .achievementType(type)
                .xpAwarded(xp)
                .imageUrl(request.getImageUrl())
                .certificateUrl(request.getCertificateUrl())
                .verificationStatus(VerificationStatus.PENDING)
                .build();

        postRepository.save(post);

        // Award base XP
        user.setXp(user.getXp() + xp);
        user.setLevel(user.getXp() / 100);
        userRepository.save(user);

        // Update achievement streak
        streakService.updateStreak(user);

        return mapToResponse(post, null);
    }

    // ═══════════════════════════════════════════════════════════════
    // VERIFY POST (Faculty only) + Notification
    // ═══════════════════════════════════════════════════════════════

    @Transactional
    public PostResponse verifyPost(Long postId, Long facultyId, String statusStr, String comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", facultyId));

        if (faculty.getRole() != Role.FACULTY && faculty.getRole() != Role.ADMIN) {
            throw new BadRequestException("Only faculty or admin can verify posts");
        }

        if (post.getVerificationStatus() != VerificationStatus.PENDING) {
            throw new BadRequestException("Post has already been " + post.getVerificationStatus().name().toLowerCase());
        }

        VerificationStatus status;
        try {
            status = VerificationStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status. Use VERIFIED or REJECTED");
        }

        if (status == VerificationStatus.PENDING) {
            throw new BadRequestException("Cannot set status back to PENDING");
        }

        post.setVerificationStatus(status);
        post.setVerifiedBy(faculty);
        post.setVerifiedAt(LocalDateTime.now());
        post.setVerificationComment(comment);
        postRepository.save(post);

        // Award bonus XP on verification
        if (status == VerificationStatus.VERIFIED) {
            User student = post.getUser();
            int bonus = calculateVerificationBonus(post.getAchievementType());
            student.setXp(student.getXp() + bonus);
            student.setLevel(student.getXp() / 100);
            userRepository.save(student);

            // Notify student
            notificationService.createNotification(
                    student.getId(), facultyId,
                    NotificationType.POST_VERIFIED,
                    "Your achievement '" + post.getTitle() + "' was verified by " + faculty.getName(),
                    postId);
        }

        return mapToResponse(post, null);
    }

    // ═══════════════════════════════════════════════════════════════
    // PREVENT EDITING VERIFIED POSTS
    // ═══════════════════════════════════════════════════════════════

    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        if (post.getVerificationStatus() == VerificationStatus.VERIFIED) {
            throw new BadRequestException("Cannot edit a verified post");
        }

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setImageUrl(request.getImageUrl());
        post.setCertificateUrl(request.getCertificateUrl());
        postRepository.save(post);

        return mapToResponse(post, null);
    }

    // ═══════════════════════════════════════════════════════════════
    // FEED & QUERIES
    // ═══════════════════════════════════════════════════════════════

    public List<PostResponse> getFeed(Integer limit) {
        return getFeedForUser(limit, null);
    }

    public List<PostResponse> getFeedForUser(Integer limit, Long currentUserId) {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponse> feed = posts.stream()
                .map(p -> mapToResponse(p, currentUserId))
                .collect(Collectors.toList());

        if (limit != null && limit > 0 && limit < feed.size()) {
            return feed.subList(0, limit);
        }
        return feed;
    }

    public List<PostResponse> getPostsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(p -> mapToResponse(p, null)).collect(Collectors.toList());
    }

    public List<PostResponse> getPendingPosts() {
        return postRepository.findByVerificationStatusOrderByCreatedAtDesc(VerificationStatus.PENDING)
                .stream().map(p -> mapToResponse(p, null)).collect(Collectors.toList());
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        postRepository.delete(post);
    }

    // ═══════════════════════════════════════════════════════════════
    // MAPPER (includes engagement metrics)
    // ═══════════════════════════════════════════════════════════════

    private PostResponse mapToResponse(Post post, Long currentUserId) {
        long likes = postLikeRepository.countByPostId(post.getId());
        long comments = postCommentRepository.countByPostId(post.getId());
        boolean isLiked = currentUserId != null
                && postLikeRepository.existsByUserIdAndPostId(currentUserId, post.getId());

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .username(post.getUser().getName())
                .title(post.getTitle())
                .description(post.getDescription())
                .achievementType(post.getAchievementType().name())
                .xpAwarded(post.getXpAwarded())
                .imageUrl(post.getImageUrl())
                .certificateUrl(post.getCertificateUrl())
                .createdAt(post.getCreatedAt())
                .verificationStatus(post.getVerificationStatus().name())
                .verifiedByName(post.getVerifiedBy() != null ? post.getVerifiedBy().getName() : null)
                .verifiedAt(post.getVerifiedAt())
                .verificationComment(post.getVerificationComment())
                .likeCount(likes)
                .commentCount(comments)
                .isLikedByCurrentUser(isLiked)
                .build();
    }
}
