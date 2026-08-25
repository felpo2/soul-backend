package com.project.soul.user.domain.repository;

import com.project.soul.user.domain.entity.Follow;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.entity.FollowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, UUID> {
    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    Optional<Follow> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    @Query("""
            select case when count(f) > 0 then true else false end
            from Follow f
            where f.follower.id = :followerId
              and f.following.id = :followingId
              and (f.status = :status or f.status is null)
            """)
    boolean existsAcceptedRelationship(
            @Param("followerId") UUID followerId,
            @Param("followingId") UUID followingId,
            @Param("status") FollowStatus status
    );

    @Query("select count(f) from Follow f where f.following.id = :userId and (f.status = :status or f.status is null)")
    long countAcceptedFollowers(
            @Param("userId") UUID userId,
            @Param("status") FollowStatus status
    );

    @Query("select count(f) from Follow f where f.follower.id = :userId and (f.status = :status or f.status is null)")
    long countAcceptedFollowing(
            @Param("userId") UUID userId,
            @Param("status") FollowStatus status
    );

    @Query("select f from Follow f where f.following.id = :userId and (f.status = :status or f.status is null)")
    Page<Follow> findAcceptedFollowers(
            @Param("userId") UUID userId,
            @Param("status") FollowStatus status,
            Pageable pageable
    );

    @Query("select f from Follow f where f.follower.id = :userId and (f.status = :status or f.status is null)")
    Page<Follow> findAcceptedFollowing(
            @Param("userId") UUID userId,
            @Param("status") FollowStatus status,
            Pageable pageable
    );

    Page<Follow> findByFollowingIdAndStatus(
            UUID followingId,
            FollowStatus status,
            Pageable pageable
    );

    java.util.Optional<Follow> findByIdAndFollowingId(UUID id, UUID followingId);
    void deleteByFollowerOrFollowing(User follower, User following);
}
