package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.ProjectRequest;
import com.tutorial.uprit.dto.ProjectResponse;
import com.tutorial.uprit.dto.TeamRequest;
import com.tutorial.uprit.dto.TeamResponse;
import com.tutorial.uprit.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProjectController — CRUD for projects + team management.
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /** GET /api/projects — list all projects */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    /** GET /api/projects/{id} — get project by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    /**
     * POST /api/projects?userId={userId} — create a new project.
     *
     * Request body: { "title": "AI Chatbot", "description": "..." }
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            @RequestParam Long userId) {
        return new ResponseEntity<>(projectService.createProject(request, userId), HttpStatus.CREATED);
    }

    /** PUT /api/projects/{id} — update project */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    /** DELETE /api/projects/{id} — delete project */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/projects/{id}/teams — add a user to the project team.
     *
     * Request body: { "userId": 1 }
     */
    @PostMapping("/{id}/teams")
    public ResponseEntity<TeamResponse> addTeamMember(
            @PathVariable Long id,
            @Valid @RequestBody TeamRequest request) {
        return new ResponseEntity<>(projectService.addTeamMember(id, request), HttpStatus.CREATED);
    }

    /** GET /api/projects/{id}/teams — get all team members for a project */
    @GetMapping("/{id}/teams")
    public ResponseEntity<List<TeamResponse>> getProjectTeam(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectTeam(id));
    }
}
