package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.TeamRequest;
import com.tutorial.uprit.model.TeamRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRequestRepository extends JpaRepository<TeamRequest, Long> {

    List<TeamRequest> findByStatusOrderByCreatedAtDesc(TeamRequestStatus status);

    List<TeamRequest> findByCreatedByIdOrderByCreatedAtDesc(Long userId);
}
