package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.ChallengeRequest;
import com.tutorial.uprit.dto.ChallengeResponse;
import com.tutorial.uprit.service.ChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ChallengeController — full CRUD for challenges.
 */
@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    /** GET /api/challenges — list all challenges */
    @GetMapping
    public ResponseEntity<List<ChallengeResponse>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }

    /** GET /api/challenges/{id} — get challenge by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponse> getChallengeById(@PathVariable Long id) {
        return ResponseEntity.ok(challengeService.getChallengeById(id));
    }

    /**
     * POST /api/challenges — create a new challenge.
     *
     * Request body:
     * {
     *   "title": "Complete Java Basics",
     *   "description": "Solve 10 Java problems",
     *   "xpReward": 100,
     *   "deadline": "2026-04-01T23:59:00"
     * }
     */
    @PostMapping
    public ResponseEntity<ChallengeResponse> createChallenge(@Valid @RequestBody ChallengeRequest request) {
        return new ResponseEntity<>(challengeService.createChallenge(request), HttpStatus.CREATED);
    }

    /** PUT /api/challenges/{id} — update challenge */
    @PutMapping("/{id}")
    public ResponseEntity<ChallengeResponse> updateChallenge(
            @PathVariable Long id,
            @Valid @RequestBody ChallengeRequest request) {
        return ResponseEntity.ok(challengeService.updateChallenge(id, request));
    }

    /** DELETE /api/challenges/{id} — delete challenge */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }
}
