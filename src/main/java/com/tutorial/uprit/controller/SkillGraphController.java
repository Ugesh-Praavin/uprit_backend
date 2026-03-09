package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.SimilarStudentResponse;
import com.tutorial.uprit.service.SkillGraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SkillGraphController — discover students with similar skills.
 */
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillGraphController {

    private final SkillGraphService skillGraphService;

    /**
     * GET /api/skills/similar/{userId}?limit=20
     * Returns students who share skills with the given user, ordered by match
     * count.
     */
    @GetMapping("/similar/{userId}")
    public ResponseEntity<List<SimilarStudentResponse>> getSimilarStudents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(skillGraphService.getSimilarStudents(userId, limit));
    }
}
