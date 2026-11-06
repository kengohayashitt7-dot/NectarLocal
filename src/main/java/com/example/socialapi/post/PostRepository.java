package com.example.socialapi.post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // @EntityGraph fetches the author in the same query (avoids the N+1 problem when listing).
    @EntityGraph(attributePaths = "author")
    List<Post> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    List<Post> findByAuthorIdOrderByCreatedAtDescIdDesc(Long authorId, Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Optional<Post> findWithAuthorById(Long id);
}
