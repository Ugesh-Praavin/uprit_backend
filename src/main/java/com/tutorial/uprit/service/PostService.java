package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.PostRequest;
import com.tutorial.uprit.dto.PostResponse;
import com.tutorial.uprit.exception.BadRequestException;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.AchievementType;
import com.tutorial.uprit.model.Post;
import com.tutorial.uprit.model.User;
import com.tutorial.uprit.repository.PostRepository;
import com.tutorial.uprit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PostService — handles achievement feed operations.
 * Contains the XP rules engine that calculates XP server-side only.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // ================================================================
    // XP RULES ENGINE — XP is NEVER accepted from the client.
    // ================================================================

    /**
     * Calculate XP based on achievement type.
     * This is the single source of truth for XP rewards.
     */
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

    // ================================================================
    // CREATE POST (with automatic XP award)
    // ================================================================

    /**
     * Create a new achievement post.
     * 1. Parse & validate achievement type
     * 2. Calculate XP via rules engine
     * 3. Save the post
     * 4. Add XP to user and recalculate level
     */
    @Transactional
    public PostResponse createPost(Long userId, PostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Parse achievement type (with friendly error)
        AchievementType type;
        try {
            type = AchievementType.valueOf(request.getAchievementType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid achievement type: " + request.getAchievementType()
                            + ". Valid types: HACKATHON_WIN, HACKATHON_PARTICIPATION, CERTIFICATE, "
                            + "PROJECT_COMPLETION, INTERNSHIP, RESEARCH_PAPER, OTHER");
        }

        // Calculate XP server-side (never from client)
        int xp = calculateXp(type);

        // Build and save the post
        Post post = Post.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .achievementType(type)
                .xpAwarded(xp)
                .imageUrl(request.getImageUrl())
                .build();

        postRepository.save(post);

        // Award XP to user and recalculate level
        user.setXp(user.getXp() + xp);
        user.setLevel(user.getXp() / 100);
        userRepository.save(user);

        return mapToResponse(post);
    }

    // ================================================================
    // FEED — All posts, most recent first
    // ================================================================

    public List<PostResponse> getFeed(Integer limit) {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        List<PostResponse> feed = posts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        // Optional limit
        if (limit != null && limit > 0 && limit < feed.size()) {
            return feed.subList(0, limit);
        }
        return feed;
    }

    // ================================================================
    // USER POSTS
    // ================================================================

    public List<PostResponse> getPostsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return postRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ================================================================
    // DELETE POST
    // ================================================================

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        postRepository.delete(post);
    }

    // ================================================================
    // MAPPER
    // ================================================================

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
                .createdAt(post.getCreatedAt())
                .build();
    }
}
