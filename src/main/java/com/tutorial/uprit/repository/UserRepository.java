package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.Role;
import com.tutorial.uprit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /** Leaderboard: users ordered by XP descending */
    List<User> findAllByOrderByXpDesc();

    /** Find students by role */
    List<User> findByRole(Role role);

    /** Faculty talent filter — dynamic JPQL */
    @Query("SELECT u FROM User u WHERE u.role = :role " +
            "AND (:department IS NULL OR u.department = :department) " +
            "AND (:year IS NULL OR u.year = :year) " +
            "AND (:xpMin IS NULL OR u.xp >= :xpMin) " +
            "AND (:xpMax IS NULL OR u.xp <= :xpMax) " +
            "ORDER BY u.xp DESC")
    List<User> filterStudents(
            @Param("role") Role role,
            @Param("department") String department,
            @Param("year") Integer year,
            @Param("xpMin") Integer xpMin,
            @Param("xpMax") Integer xpMax);
}
