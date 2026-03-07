package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.*;
import com.tutorial.uprit.exception.BadRequestException;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.Skill;
import com.tutorial.uprit.model.User;
import com.tutorial.uprit.model.UserSkill;
import com.tutorial.uprit.repository.SkillRepository;
import com.tutorial.uprit.repository.UserRepository;
import com.tutorial.uprit.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SkillService — CRUD for skills + assigning skills to users.
 */
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;

    // ── CRUD ───────────────────────────────────────────

    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SkillResponse getSkillById(Long id) {
        Skill skill = findSkillOrThrow(id);
        return mapToResponse(skill);
    }

    public SkillResponse createSkill(SkillRequest request) {
        if (skillRepository.existsBySkillName(request.getSkillName())) {
            throw new BadRequestException("Skill already exists: " + request.getSkillName());
        }
        Skill skill = Skill.builder()
                .skillName(request.getSkillName())
                .build();
        skillRepository.save(skill);
        return mapToResponse(skill);
    }

    public SkillResponse updateSkill(Long id, SkillRequest request) {
        Skill skill = findSkillOrThrow(id);
        skill.setSkillName(request.getSkillName());
        skillRepository.save(skill);
        return mapToResponse(skill);
    }

    public void deleteSkill(Long id) {
        Skill skill = findSkillOrThrow(id);
        skillRepository.delete(skill);
    }

    // ── Assign Skill to User ───────────────────────────

    /**
     * Assign a skill to a user with a proficiency level.
     */
    public UserSkillResponse assignSkillToUser(Long userId, UserSkillRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", request.getSkillId()));

        // Check if skill is already assigned
        if (userSkillRepository.existsByUserIdAndSkillId(userId, request.getSkillId())) {
            throw new BadRequestException("Skill already assigned to this user");
        }

        UserSkill userSkill = UserSkill.builder()
                .user(user)
                .skill(skill)
                .proficiencyLevel(request.getProficiencyLevel())
                .build();

        userSkillRepository.save(userSkill);

        return UserSkillResponse.builder()
                .id(userSkill.getId())
                .userId(user.getId())
                .userName(user.getName())
                .skillId(skill.getId())
                .skillName(skill.getSkillName())
                .proficiencyLevel(userSkill.getProficiencyLevel())
                .build();
    }

    /**
     * Get all skills assigned to a user.
     */
    public List<UserSkillResponse> getUserSkills(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return userSkillRepository.findByUserId(userId).stream()
                .map(us -> UserSkillResponse.builder()
                        .id(us.getId())
                        .userId(us.getUser().getId())
                        .userName(us.getUser().getName())
                        .skillId(us.getSkill().getId())
                        .skillName(us.getSkill().getSkillName())
                        .proficiencyLevel(us.getProficiencyLevel())
                        .build())
                .collect(Collectors.toList());
    }

    // ── Helpers ────────────────────────────────────────

    private Skill findSkillOrThrow(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));
    }

    private SkillResponse mapToResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .skillName(skill.getSkillName())
                .build();
    }
}
