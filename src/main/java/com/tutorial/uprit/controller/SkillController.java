package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.*;
import com.tutorial.uprit.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SkillController — CRUD for skills + assign skill to user.
 */
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    /** GET /api/skills — list all skills */
    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    /** GET /api/skills/{id} — get skill by ID */
    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse> getSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    /** POST /api/skills — create a new skill */
    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(@Valid @RequestBody SkillRequest request) {
        return new ResponseEntity<>(skillService.createSkill(request), HttpStatus.CREATED);
    }

    /** PUT /api/skills/{id} — update skill */
    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(skillService.updateSkill(id, request));
    }

    /** DELETE /api/skills/{id} — delete skill */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/skills/users/{userId} — assign a skill to a user.
     *
     * Request body: { "skillId": 1, "proficiencyLevel": "INTERMEDIATE" }
     */
    @PostMapping("/users/{userId}")
    public ResponseEntity<UserSkillResponse> assignSkillToUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserSkillRequest request) {
        return new ResponseEntity<>(skillService.assignSkillToUser(userId, request), HttpStatus.CREATED);
    }

    /** GET /api/skills/users/{userId} — get all skills for a user */
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<UserSkillResponse>> getUserSkills(@PathVariable Long userId) {
        return ResponseEntity.ok(skillService.getUserSkills(userId));
    }
}
