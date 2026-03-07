package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /** Leaderboard: users ordered by XP descending */
    List<User> findAllByOrderByXpDesc();
}
