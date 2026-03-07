package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.*;
import com.tutorial.uprit.exception.BadRequestException;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.Project;
import com.tutorial.uprit.model.ProjectStatus;
import com.tutorial.uprit.model.Team;
import com.tutorial.uprit.model.User;
import com.tutorial.uprit.repository.ProjectRepository;
import com.tutorial.uprit.repository.TeamRepository;
import com.tutorial.uprit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProjectService — CRUD for projects + team management.
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    // ── CRUD ───────────────────────────────────────────

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = findProjectOrThrow(id);
        return mapToResponse(project);
    }

    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .createdBy(user)
                .status(ProjectStatus.OPEN)
                .build();

        projectRepository.save(project);
        return mapToResponse(project);
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = findProjectOrThrow(id);
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            try {
                project.setStatus(ProjectStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + request.getStatus()
                        + ". Valid values: OPEN, IN_PROGRESS, COMPLETED");
            }
        }

        projectRepository.save(project);
        return mapToResponse(project);
    }

    public void deleteProject(Long id) {
        Project project = findProjectOrThrow(id);
        projectRepository.delete(project);
    }

    // ── Team Management ────────────────────────────────

    /**
     * Add a user to a project's team.
     */
    public TeamResponse addTeamMember(Long projectId, TeamRequest request) {
        Project project = findProjectOrThrow(projectId);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // Check if user is already on the team
        if (teamRepository.existsByProjectIdAndUserId(projectId, request.getUserId())) {
            throw new BadRequestException("User is already a member of this project's team");
        }

        Team team = Team.builder()
                .project(project)
                .user(user)
                .build();

        teamRepository.save(team);

        return TeamResponse.builder()
                .id(team.getId())
                .projectId(project.getId())
                .projectTitle(project.getTitle())
                .userId(user.getId())
                .userName(user.getName())
                .build();
    }

    /**
     * Get all team members for a project.
     */
    public List<TeamResponse> getProjectTeam(Long projectId) {
        findProjectOrThrow(projectId);

        return teamRepository.findByProjectId(projectId).stream()
                .map(team -> TeamResponse.builder()
                        .id(team.getId())
                        .projectId(team.getProject().getId())
                        .projectTitle(team.getProject().getTitle())
                        .userId(team.getUser().getId())
                        .userName(team.getUser().getName())
                        .build())
                .collect(Collectors.toList());
    }

    // ── Helpers ────────────────────────────────────────

    private Project findProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .createdById(project.getCreatedBy().getId())
                .createdByName(project.getCreatedBy().getName())
                .status(project.getStatus().name())
                .build();
    }
}
