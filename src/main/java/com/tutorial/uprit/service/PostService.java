package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.PostRequest;
import com.tutorial.uprit.dto.PostResponse;
import com.tutorial.uprit.exception.BadRequestException;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.PostRepository;
import com.tutorial.uprit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PostService — handles achievement feed and verification operations.
 * Contains the XP rules engine and verification bonus system.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

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

    /** Bonus XP awarded when faculty verifies a post (50% of base XP) */
    private int calculateVerificationBonus(AchievementType type) {
        return calculateXp(type) / 2;
    }

    // ═══════════════════════════════════════════════════════════════
    // CREATE POST
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

        return mapToResponse(post);
    }

    // ═══════════════════════════════════════════════════════════════
    // VERIFY POST (Faculty only)
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
        }

        return mapToResponse(post);
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

        return mapToResponse(post);
    }

    // ═══════════════════════════════════════════════════════════════
    // FEED & QUERIES
    // ═══════════════════════════════════════════════════════════════

    public List<PostResponse> getFeed(Integer limit) {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponse> feed = posts.stream().map(this::mapToResponse).collect(Collectors.toList());

        if (limit != null && limit > 0 && limit < feed.size()) {
            return feed.subList(0, limit);
        }
        return feed;
    }

    public List<PostResponse> getPostsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<PostResponse> getPendingPosts() {
        return postRepository.findByVerificationStatusOrderByCreatedAtDesc(VerificationStatus.PENDING)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        postRepository.delete(post);
    }

    // ═══════════════════════════════════════════════════════════════
    // MAPPER
    // ═══════════════════════════════════════════════════════════════

    private PostResponse mapToResponse(Post post) {
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
                .build();
    }
}
