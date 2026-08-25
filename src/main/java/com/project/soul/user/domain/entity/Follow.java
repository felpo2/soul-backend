package com.project.soul.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "follow_relationship",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_follow_follower_following",
                columnNames = {"follower_id", "following_id"}
        )
)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @Column(nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private FollowStatus status;

    public Follow(User follower, User following, Instant createdAt, FollowStatus status) {
        this.follower = follower;
        this.following = following;
        this.createdAt = createdAt;
        this.status = status;
    }

    public boolean isAccepted() {
        return status == null || status == FollowStatus.ACCEPTED;
    }

    public void accept() {
        status = FollowStatus.ACCEPTED;
    }
}
