package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.ReportRequest;
import com.tutorial.uprit.dto.ReportResponse;
import com.tutorial.uprit.exception.BadRequestException;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.PostRepository;
import com.tutorial.uprit.repository.ReportRepository;
import com.tutorial.uprit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReportResponse createReport(Long facultyId, ReportRequest request) {
        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", facultyId));

        if (faculty.getRole() != Role.FACULTY && faculty.getRole() != Role.ADMIN) {
            throw new BadRequestException("Only faculty or admin can file reports");
        }

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", request.getPostId()));

        ReportReason reason;
        try {
            reason = ReportReason.valueOf(request.getReason().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid reason: " + request.getReason()
                    + ". Valid: SPAM, FAKE_ACHIEVEMENT, INAPPROPRIATE_CONTENT, OTHER");
        }

        Report report = Report.builder()
                .post(post)
                .faculty(faculty)
                .reason(reason)
                .details(request.getDetails())
                .status(ReportStatus.OPEN)
                .build();

        reportRepository.save(report);
        return mapToResponse(report);
    }

    public List<ReportResponse> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ReportResponse> getOpenReports() {
        return reportRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.OPEN)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public ReportResponse updateReportStatus(Long reportId, String statusStr) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", reportId));

        ReportStatus status;
        try {
            status = ReportStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status. Use OPEN, REVIEWED, or DISMISSED");
        }

        report.setStatus(status);
        reportRepository.save(report);
        return mapToResponse(report);
    }

    private ReportResponse mapToResponse(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .postId(report.getPost().getId())
                .postTitle(report.getPost().getTitle())
                .facultyId(report.getFaculty().getId())
                .facultyName(report.getFaculty().getName())
                .reason(report.getReason().name())
                .details(report.getDetails())
                .status(report.getStatus().name())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
