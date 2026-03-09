package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.ConnectionResponse;
import com.tutorial.uprit.exception.BadRequestException;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.ConnectionRepository;
import com.tutorial.uprit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * Send connection request.
     * Validates: no self-connect, no duplicate (both directions).
     */
    @Transactional
    public ConnectionResponse sendRequest(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new BadRequestException("Cannot send connection request to yourself");
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requesterId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", receiverId));

        // Check both directions: A→B and B→A
        connectionRepository.findByUserPair(requesterId, receiverId).ifPresent(existing -> {
            throw new BadRequestException("Connection already exists (status: " + existing.getStatus() + ")");
        });

        Connection conn = Connection.builder()
                .requester(requester)
                .receiver(receiver)
                .status(ConnectionStatus.PENDING)
                .build();

        connectionRepository.save(conn);

        // Notify receiver about connection request
        notificationService.createNotification(
                receiverId, requesterId,
                NotificationType.CONNECTION_REQUEST,
                requester.getName() + " sent you a connection request",
                conn.getId());

        return mapToResponse(conn, receiverId);
    }

    @Transactional
    public ConnectionResponse acceptRequest(Long connectionId) {
        Connection conn = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection", "id", connectionId));

        if (conn.getStatus() != ConnectionStatus.PENDING) {
            throw new BadRequestException("Request has already been " + conn.getStatus().name().toLowerCase());
        }

        conn.setStatus(ConnectionStatus.ACCEPTED);
        connectionRepository.save(conn);
        return mapToResponse(conn, conn.getRequester().getId());
    }

    @Transactional
    public ConnectionResponse rejectRequest(Long connectionId) {
        Connection conn = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection", "id", connectionId));

        if (conn.getStatus() != ConnectionStatus.PENDING) {
            throw new BadRequestException("Request has already been " + conn.getStatus().name().toLowerCase());
        }

        conn.setStatus(ConnectionStatus.REJECTED);
        connectionRepository.save(conn);
        return mapToResponse(conn, conn.getRequester().getId());
    }

    /** Pending requests received by this user */
    public List<ConnectionResponse> getPendingRequests(Long userId) {
        return connectionRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(userId, ConnectionStatus.PENDING)
                .stream()
                .map(c -> mapToResponse(c, c.getRequester().getId()))
                .collect(Collectors.toList());
    }

    /** Accepted connections for this user */
    public List<ConnectionResponse> getMyConnections(Long userId) {
        return connectionRepository.findAcceptedConnections(userId)
                .stream()
                .map(c -> {
                    // Show the OTHER user
                    Long otherId = c.getRequester().getId().equals(userId)
                            ? c.getReceiver().getId()
                            : c.getRequester().getId();
                    return mapToResponse(c, otherId);
                })
                .collect(Collectors.toList());
    }

    private ConnectionResponse mapToResponse(Connection conn, Long otherUserId) {
        User other = conn.getRequester().getId().equals(otherUserId)
                ? conn.getRequester()
                : conn.getReceiver();
        return ConnectionResponse.builder()
                .id(conn.getId())
                .userId(other.getId())
                .userName(other.getName())
                .department(other.getDepartment())
                .avatarUrl(other.getAvatarUrl())
                .xp(other.getXp())
                .status(conn.getStatus().name())
                .createdAt(conn.getCreatedAt())
                .build();
    }
}
