package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.FollowResponse;
import com.tutorial.uprit.exception.BadRequestException;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.FollowRepository;
import com.tutorial.uprit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public FollowResponse follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BadRequestException("Cannot follow yourself");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", followerId));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", followingId));

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new BadRequestException("Already following this user");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        // Notify the user being followed
        notificationService.createNotification(
                followingId, followerId,
                NotificationType.FOLLOW,
                follower.getName() + " started following you",
                followerId);

        return mapToResponse(following);
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new BadRequestException("You are not following this user"));
        followRepository.delete(follow);
    }

    public List<FollowResponse> getFollowers(Long userId) {
        return followRepository.findByFollowingId(userId).stream()
                .map(f -> mapToResponse(f.getFollower()))
                .collect(Collectors.toList());
    }

    public List<FollowResponse> getFollowing(Long userId) {
        return followRepository.findByFollowerId(userId).stream()
                .map(f -> mapToResponse(f.getFollowing()))
                .collect(Collectors.toList());
    }

    private FollowResponse mapToResponse(User user) {
        return FollowResponse.builder()
                .id(user.getId())
                .userId(user.getId())
                .userName(user.getName())
                .department(user.getDepartment())
                .avatarUrl(user.getAvatarUrl())
                .xp(user.getXp())
                .level(user.getLevel())
                .build();
    }
}
