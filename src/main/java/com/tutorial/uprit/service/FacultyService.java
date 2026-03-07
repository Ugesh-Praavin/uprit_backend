package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.StudentFilterResponse;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.PostRepository;
import com.tutorial.uprit.repository.UserRepository;
import com.tutorial.uprit.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * FacultyService — talent discovery with filtering.
 */
@Service
@RequiredArgsConstructor
public class FacultyService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserSkillRepository userSkillRepository;

    /**
     * Filter students based on multiple optional criteria.
     */
    public List<StudentFilterResponse> filterStudents(
            String department, Integer year,
            Integer xpMin, Integer xpMax,
            Boolean verifiedOnly) {

        List<User> students = userRepository.filterStudents(
                Role.USER, department, year, xpMin, xpMax);

        return students.stream().map(user -> {
            long totalPosts = postRepository.countByUserId(user.getId());
            long verifiedPosts = postRepository.countByUserIdAndVerificationStatus(
                    user.getId(), VerificationStatus.VERIFIED);

            // If verifiedOnly filter is set, skip users with no verified posts
            if (Boolean.TRUE.equals(verifiedOnly) && verifiedPosts == 0) {
                return null;
            }

            // Get user skills
            List<String> skills = userSkillRepository.findByUserId(user.getId())
                    .stream()
                    .map(us -> us.getSkill().getSkillName())
                    .collect(Collectors.toList());

            return StudentFilterResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .department(user.getDepartment())
                    .year(user.getYear())
                    .xp(user.getXp())
                    .level(user.getLevel())
                    .role(user.getRole().name())
                    .totalPosts((int) totalPosts)
                    .verifiedPosts((int) verifiedPosts)
                    .skills(skills)
                    .build();
        }).filter(s -> s != null).collect(Collectors.toList());
    }
}
