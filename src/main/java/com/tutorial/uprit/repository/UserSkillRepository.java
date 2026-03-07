package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    List<UserSkill> findByUserId(Long userId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);
}
