package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.*;
import com.tutorial.uprit.service.FacultyService;
import com.tutorial.uprit.service.PostService;
import com.tutorial.uprit.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FacultyController — faculty-only operations:
 * verification, reporting, and talent discovery.
 */
@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final PostService postService;
    private final ReportService reportService;
    private final FacultyService facultyService;

    // ═══════════════════════════════════════
    // VERIFICATION
    // ═══════════════════════════════════════

    /** Get all pending posts for verification */
    @GetMapping("/pending")
    public ResponseEntity<List<PostResponse>> getPendingPosts() {
        return ResponseEntity.ok(postService.getPendingPosts());
    }

    /** Verify or reject a post */
    @PostMapping("/verify/{postId}")
    public ResponseEntity<PostResponse> verifyPost(
            @PathVariable Long postId,
            @RequestParam Long facultyId,
            @Valid @RequestBody VerifyPostRequest request) {
        return ResponseEntity.ok(postService.verifyPost(
                postId, facultyId, request.getStatus(), request.getComment()));
    }

    // ═══════════════════════════════════════
    // REPORTS
    // ═══════════════════════════════════════

    /** Create a new report */
    @PostMapping("/reports")
    public ResponseEntity<ReportResponse> createReport(
            @RequestParam Long facultyId,
            @Valid @RequestBody ReportRequest request) {
        return new ResponseEntity<>(reportService.createReport(facultyId, request), HttpStatus.CREATED);
    }

    /** Get all reports */
    @GetMapping("/reports")
    public ResponseEntity<List<ReportResponse>> getReports(
            @RequestParam(required = false) String status) {
        if ("OPEN".equalsIgnoreCase(status)) {
            return ResponseEntity.ok(reportService.getOpenReports());
        }
        return ResponseEntity.ok(reportService.getAllReports());
    }

    /** Update report status */
    @PutMapping("/reports/{reportId}")
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable Long reportId,
            @RequestParam String status) {
        return ResponseEntity.ok(reportService.updateReportStatus(reportId, status));
    }

    // ═══════════════════════════════════════
    // TALENT DISCOVERY
    // ═══════════════════════════════════════

    /** Filter students for talent discovery */
    @GetMapping("/students")
    public ResponseEntity<List<StudentFilterResponse>> filterStudents(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer xpMin,
            @RequestParam(required = false) Integer xpMax,
            @RequestParam(required = false) Boolean verifiedOnly) {
        return ResponseEntity.ok(facultyService.filterStudents(
                department, year, xpMin, xpMax, verifiedOnly));
    }
}
