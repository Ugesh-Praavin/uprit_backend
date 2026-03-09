package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.TeamRequestDTO;
import com.tutorial.uprit.dto.TeamRequestResponse;
import com.tutorial.uprit.service.TeamFormationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TeamRequestController — team formation CRUD + join/leave.
 */
@RestController
@RequestMapping("/api/team-requests")
@RequiredArgsConstructor
public class TeamRequestController {

    private final TeamFormationService teamFormationService;

    /** POST /api/team-requests?userId={userId} — create team request */
    @PostMapping
    public ResponseEntity<TeamRequestResponse> createTeamRequest(
            @RequestParam Long userId,
            @Valid @RequestBody TeamRequestDTO dto) {
        return new ResponseEntity<>(teamFormationService.createTeamRequest(userId, dto), HttpStatus.CREATED);
    }

    /** GET /api/team-requests — all open requests */
    @GetMapping
    public ResponseEntity<List<TeamRequestResponse>> getOpenRequests() {
        return ResponseEntity.ok(teamFormationService.getOpenRequests());
    }

    /** GET /api/team-requests/{id} — single request with members */
    @GetMapping("/{id}")
    public ResponseEntity<TeamRequestResponse> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(teamFormationService.getRequestById(id));
    }

    /** POST /api/team-requests/{id}/join?userId={userId} — join a team */
    @PostMapping("/{id}/join")
    public ResponseEntity<TeamRequestResponse> joinTeamRequest(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(teamFormationService.joinTeamRequest(userId, id));
    }

    /** DELETE /api/team-requests/{id}/leave?userId={userId} — leave a team */
    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveTeamRequest(
            @PathVariable Long id,
            @RequestParam Long userId) {
        teamFormationService.leaveTeamRequest(userId, id);
        return ResponseEntity.noContent().build();
    }

    /** PUT /api/team-requests/{id}/close?userId={userId} — close a request */
    @PutMapping("/{id}/close")
    public ResponseEntity<TeamRequestResponse> closeTeamRequest(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(teamFormationService.closeTeamRequest(userId, id));
    }

    /** GET /api/team-requests/user/{userId} — requests by a specific user */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TeamRequestResponse>> getRequestsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(teamFormationService.getRequestsByUser(userId));
    }
}
