package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.ConnectionResponse;
import com.tutorial.uprit.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ConnectionController — mutual connection system (LinkedIn-style).
 */
@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;

    /** Send connection request */
    @PostMapping("/request")
    public ResponseEntity<ConnectionResponse> sendRequest(
            @RequestParam Long requesterId,
            @RequestParam Long receiverId) {
        return new ResponseEntity<>(connectionService.sendRequest(requesterId, receiverId), HttpStatus.CREATED);
    }

    /** Get my pending requests (received) */
    @GetMapping("/requests")
    public ResponseEntity<List<ConnectionResponse>> getPendingRequests(@RequestParam Long userId) {
        return ResponseEntity.ok(connectionService.getPendingRequests(userId));
    }

    /** Accept a connection request */
    @PostMapping("/accept/{id}")
    public ResponseEntity<ConnectionResponse> acceptRequest(@PathVariable Long id) {
        return ResponseEntity.ok(connectionService.acceptRequest(id));
    }

    /** Reject a connection request */
    @PostMapping("/reject/{id}")
    public ResponseEntity<ConnectionResponse> rejectRequest(@PathVariable Long id) {
        return ResponseEntity.ok(connectionService.rejectRequest(id));
    }

    /** Get my accepted connections */
    @GetMapping("/list")
    public ResponseEntity<List<ConnectionResponse>> getMyConnections(@RequestParam Long userId) {
        return ResponseEntity.ok(connectionService.getMyConnections(userId));
    }
}
