package com.project.soul.posts.domain.repository;

import com.project.soul.posts.domain.entity.Post;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.entity.FollowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PostRepository extends JpaRepository<Post,UUID> {

    Page<Post> findByUserId(UUID userId, Pageable pageable);
    long countByUserId(UUID userId);
    void deleteByUser(User user);

    @Query("""
            select p from Post p
            where p.user.id = :userId
               or p.user.id in (
                    select f.following.id from Follow f
                    where f.follower.id = :userId
                      and (f.status = :status or f.status is null)
               )
            """)
    Page<Post> findFeed(
            @Param("userId") UUID userId,
            @Param("status") FollowStatus status,
            Pageable pageable
    );

}
