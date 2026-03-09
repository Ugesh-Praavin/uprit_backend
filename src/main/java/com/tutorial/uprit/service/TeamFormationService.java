package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.TeamMemberResponse;
import com.tutorial.uprit.dto.TeamRequestDTO;
import com.tutorial.uprit.dto.TeamRequestResponse;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.TeamMemberRepository;
import com.tutorial.uprit.repository.TeamRequestRepository;
import com.tutorial.uprit.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TeamFormationService — create, join, leave, close team requests.
 * Includes all validations from plan: self-join, full team, closed team, leader
 * leave prevention.
 */
@Service
@RequiredArgsConstructor
public class TeamFormationService {

    private final TeamRequestRepository teamRequestRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * Create a team request. Creator auto-added as LEADER.
     */
    @Transactional
    public TeamRequestResponse createTeamRequest(Long userId, TeamRequestDTO dto) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TeamRequest tr = TeamRequest.builder()
                .createdBy(creator)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .requiredSkills(dto.getRequiredSkills())
                .maxMembers(dto.getMaxMembers())
                .eventId(dto.getEventId())
                .build();
        teamRequestRepository.save(tr);

        // Auto-add creator as LEADER
        TeamMember leader = TeamMember.builder()
                .teamRequest(tr)
                .user(creator)
                .role(TeamRole.LEADER)
                .build();
        teamMemberRepository.save(leader);

        return mapToResponse(tr);
    }

    /**
     * Join a team request. Validates: not self, not full, not closed, not already
     * joined.
     */
    @Transactional
    public TeamRequestResponse joinTeamRequest(Long userId, Long requestId) {
        TeamRequest tr = teamRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Team request not found"));
        User joiner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validation: closed
        if (tr.getStatus() == TeamRequestStatus.CLOSED) {
            throw new RuntimeException("This team request is closed");
        }

        // Validation: creator trying to join again
        if (tr.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("You are already the team leader");
        }

        // Validation: already joined
        if (teamMemberRepository.existsByTeamRequestIdAndUserId(requestId, userId)) {
            throw new RuntimeException("You have already joined this team");
        }

        // Validation: full
        long currentCount = teamMemberRepository.countByTeamRequestId(requestId);
        if (currentCount >= tr.getMaxMembers()) {
            throw new RuntimeException("This team is full (" + tr.getMaxMembers() + " members)");
        }

        // Join
        TeamMember member = TeamMember.builder()
                .teamRequest(tr)
                .user(joiner)
                .role(TeamRole.MEMBER)
                .build();
        teamMemberRepository.save(member);

        // Notify team creator
        notificationService.createNotification(
                tr.getCreatedBy().getId(), userId,
                NotificationType.CONNECTION_REQUEST,
                joiner.getName() + " joined your team: " + tr.getTitle(),
                tr.getId());

        return mapToResponse(tr);
    }

    /**
     * Leave a team request. Leader cannot leave.
     */
    @Transactional
    public void leaveTeamRequest(Long userId, Long requestId) {
        TeamMember member = teamMemberRepository.findByTeamRequestIdAndUserId(requestId, userId)
                .orElseThrow(() -> new RuntimeException("You are not a member of this team"));

        if (member.getRole() == TeamRole.LEADER) {
            throw new RuntimeException("Leader cannot leave. Close the team instead.");
        }

        teamMemberRepository.delete(member);
    }

    /**
     * Close team request. Only creator can close.
     */
    @Transactional
    public TeamRequestResponse closeTeamRequest(Long userId, Long requestId) {
        TeamRequest tr = teamRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Team request not found"));

        if (!tr.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Only the team creator can close the request");
        }

        tr.setStatus(TeamRequestStatus.CLOSED);
        teamRequestRepository.save(tr);
        return mapToResponse(tr);
    }

    /** Get all open team requests */
    public List<TeamRequestResponse> getOpenRequests() {
        return teamRequestRepository.findByStatusOrderByCreatedAtDesc(TeamRequestStatus.OPEN)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /** Get team requests created by a specific user */
    public List<TeamRequestResponse> getRequestsByUser(Long userId) {
        return teamRequestRepository.findByCreatedByIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /** Get single team request by ID */
    public TeamRequestResponse getRequestById(Long requestId) {
        TeamRequest tr = teamRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Team request not found"));
        return mapToResponse(tr);
    }

    // ── Mapping ────────────────────────────────────────

    private TeamRequestResponse mapToResponse(TeamRequest tr) {
        List<TeamMember> members = teamMemberRepository.findByTeamRequestId(tr.getId());
        List<TeamMemberResponse> memberDtos = members.stream()
                .map(m -> TeamMemberResponse.builder()
                        .id(m.getId())
                        .userId(m.getUser().getId())
                        .userName(m.getUser().getName())
                        .avatarUrl(m.getUser().getAvatarUrl())
                        .role(m.getRole().name())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .collect(Collectors.toList());

        List<String> skills = Arrays.stream(tr.getRequiredSkills().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        return TeamRequestResponse.builder()
                .id(tr.getId())
                .createdById(tr.getCreatedBy().getId())
                .createdByName(tr.getCreatedBy().getName())
                .createdByAvatarUrl(tr.getCreatedBy().getAvatarUrl())
                .title(tr.getTitle())
                .description(tr.getDescription())
                .requiredSkills(skills)
                .maxMembers(tr.getMaxMembers())
                .currentMembers(members.size())
                .eventId(tr.getEventId())
                .status(tr.getStatus().name())
                .createdAt(tr.getCreatedAt())
                .members(memberDtos)
                .build();
    }
}
