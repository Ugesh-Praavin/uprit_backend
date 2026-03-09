package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.PostResponse;
import com.tutorial.uprit.dto.UserProfileResponse;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProfileService — aggregates all profile data in a single call.
 * Avoids N+1 by using count queries instead of collection loading.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserSkillRepository userSkillRepository;
    private final FollowRepository followRepository;
    private final ConnectionRepository connectionRepository;

    public UserProfileResponse getUserProfile(Long userId, Long viewerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Count queries (efficient — no N+1)
        long totalPosts = postRepository.countByUserId(userId);
        long verifiedPosts = postRepository.countByUserIdAndVerificationStatus(userId, VerificationStatus.VERIFIED);
        long followersCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        long connectionsCount = connectionRepository.countAcceptedConnections(userId);

        // Skills
        List<String> skills = userSkillRepository.findByUserId(userId).stream()
                .map(us -> us.getSkill().getSkillName())
                .collect(Collectors.toList());

        // Recent posts (last 5)
        List<PostResponse> recentPosts = postRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .limit(5)
                .map(this::mapPost)
                .collect(Collectors.toList());

        // Viewer relationship
        Boolean isFollowing = false;
        String connectionStatus = "NONE";

        if (viewerId != null && !viewerId.equals(userId)) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(viewerId, userId);
            connectionStatus = connectionRepository.findStatusByUserPair(viewerId, userId)
                    .map(ConnectionStatus::name)
                    .orElse("NONE");
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .department(user.getDepartment())
                .avatarUrl(user.getAvatarUrl())
                .year(user.getYear())
                .xp(user.getXp())
                .level(user.getLevel())
                .coins(user.getCoins())
                .role(user.getRole().name())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .connectionsCount(connectionsCount)
                .totalPosts(totalPosts)
                .verifiedPosts(verifiedPosts)
                .skills(skills)
                .recentPosts(recentPosts)
                .isFollowing(isFollowing)
                .connectionStatus(connectionStatus)
                .build();
    }

    private PostResponse mapPost(Post post) {
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
