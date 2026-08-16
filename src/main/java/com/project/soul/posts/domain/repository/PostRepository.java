package com.project.soul.posts.domain.repository;

import com.project.soul.posts.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface PostRepository extends JpaRepository<Post,UUID> {

    Page<Post> findByUserId(UUID userId, Pageable pageable);

}
