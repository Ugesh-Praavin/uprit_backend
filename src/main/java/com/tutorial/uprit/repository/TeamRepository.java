package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByProjectId(Long projectId);

    List<Team> findByUserId(Long userId);

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
}
