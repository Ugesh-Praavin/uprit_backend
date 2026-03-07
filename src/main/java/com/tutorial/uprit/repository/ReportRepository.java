package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.Report;
import com.tutorial.uprit.model.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByOrderByCreatedAtDesc();

    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<Report> findByPostIdOrderByCreatedAtDesc(Long postId);
}
