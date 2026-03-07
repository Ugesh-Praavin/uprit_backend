package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.ChallengeRequest;
import com.tutorial.uprit.dto.ChallengeResponse;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.Challenge;
import com.tutorial.uprit.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ChallengeService — full CRUD for challenges.
 */
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public List<ChallengeResponse> getAllChallenges() {
        return challengeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ChallengeResponse getChallengeById(Long id) {
        Challenge challenge = findChallengeOrThrow(id);
        return mapToResponse(challenge);
    }

    public ChallengeResponse createChallenge(ChallengeRequest request) {
        Challenge challenge = Challenge.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .xpReward(request.getXpReward())
                .deadline(request.getDeadline())
                .build();

        challengeRepository.save(challenge);
        return mapToResponse(challenge);
    }

    public ChallengeResponse updateChallenge(Long id, ChallengeRequest request) {
        Challenge challenge = findChallengeOrThrow(id);
        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());
        challenge.setXpReward(request.getXpReward());
        challenge.setDeadline(request.getDeadline());
        challengeRepository.save(challenge);
        return mapToResponse(challenge);
    }

    public void deleteChallenge(Long id) {
        Challenge challenge = findChallengeOrThrow(id);
        challengeRepository.delete(challenge);
    }

    // ── Helpers ────────────────────────────────────────

    private Challenge findChallengeOrThrow(Long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge", "id", id));
    }

    private ChallengeResponse mapToResponse(Challenge challenge) {
        return ChallengeResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .xpReward(challenge.getXpReward())
                .deadline(challenge.getDeadline())
                .build();
    }
}
