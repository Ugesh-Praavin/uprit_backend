package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.SimilarStudentResponse;
import com.tutorial.uprit.model.Skill;
import com.tutorial.uprit.model.User;
import com.tutorial.uprit.model.UserSkill;
import com.tutorial.uprit.repository.UserRepository;
import com.tutorial.uprit.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SkillGraphService — finds students with similar skills.
 * Uses in-memory grouping for flexibility and correctness.
 */
@Service
@RequiredArgsConstructor
public class SkillGraphService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;

    /**
     * Find students who share skills with the given user.
     * Ordered by number of matching skills (descending).
     *
     * @param userId current user
     * @param limit  max results (default 20)
     */
    public List<SimilarStudentResponse> getSimilarStudents(Long userId, int limit) {
        // 1. Get current user's skills
        List<UserSkill> mySkills = userSkillRepository.findByUserId(userId);
        if (mySkills.isEmpty())
            return Collections.emptyList();

        Set<Long> mySkillIds = mySkills.stream()
                .map(us -> us.getSkill().getId())
                .collect(Collectors.toSet());

        Map<String, Long> mySkillNameToId = mySkills.stream()
                .collect(Collectors.toMap(
                        us -> us.getSkill().getSkillName(),
                        us -> us.getSkill().getId(),
                        (a, b) -> a));

        // 2. Find all UserSkills matching my skill IDs
        // Group by user → list of matching skills
        Map<Long, List<String>> userToCommonSkills = new LinkedHashMap<>();

        for (Long skillId : mySkillIds) {
            List<UserSkill> others = userSkillRepository.findBySkillId(skillId);
            for (UserSkill us : others) {
                Long otherUserId = us.getUser().getId();
                if (otherUserId.equals(userId))
                    continue; // exclude self
                userToCommonSkills
                        .computeIfAbsent(otherUserId, k -> new ArrayList<>())
                        .add(us.getSkill().getSkillName());
            }
        }

        // 3. Sort by match count descending, limit, and map to response
        return userToCommonSkills.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .limit(limit)
                .map(entry -> {
                    User u = userRepository.findById(entry.getKey()).orElse(null);
                    if (u == null)
                        return null;
                    return SimilarStudentResponse.builder()
                            .userId(u.getId())
                            .name(u.getName())
                            .department(u.getDepartment())
                            .xp(u.getXp())
                            .level(u.getLevel())
                            .avatarUrl(u.getAvatarUrl())
                            .commonSkills(entry.getValue())
                            .commonSkillCount(entry.getValue().size())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
