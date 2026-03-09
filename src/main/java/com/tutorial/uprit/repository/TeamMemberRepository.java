package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeamRequestId(Long teamRequestId);

    boolean existsByTeamRequestIdAndUserId(Long teamRequestId, Long userId);

    long countByTeamRequestId(Long teamRequestId);

    Optional<TeamMember> findByTeamRequestIdAndUserId(Long teamRequestId, Long userId);

    void deleteByTeamRequestIdAndUserId(Long teamRequestId, Long userId);
}
