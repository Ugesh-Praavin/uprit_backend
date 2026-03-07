package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.Post;
import com.tutorial.uprit.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /** Feed: all posts ordered by most recent first */
    List<Post> findAllByOrderByCreatedAtDesc();

    /** Posts by a specific user, most recent first */
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** Posts filtered by verification status */
    List<Post> findByVerificationStatusOrderByCreatedAtDesc(VerificationStatus status);

    /** Count posts by user and verification status */
    long countByUserIdAndVerificationStatus(Long userId, VerificationStatus status);

    /** Count all posts by user */
    long countByUserId(Long userId);
}
