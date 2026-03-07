package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /** Feed: all posts ordered by most recent first */
    List<Post> findAllByOrderByCreatedAtDesc();

    /** Posts by a specific user, most recent first */
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
}
