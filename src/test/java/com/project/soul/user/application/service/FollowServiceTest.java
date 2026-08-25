package com.project.soul.user.application.service;

import com.project.soul.user.application.exception.FollowAlreadyExistsException;
import com.project.soul.user.application.exception.InvalidFollowException;
import com.project.soul.user.application.dto.FollowRelationshipStatus;
import com.project.soul.user.domain.entity.Follow;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.entity.FollowStatus;
import com.project.soul.user.domain.repository.FollowRepository;
import com.project.soul.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock FollowRepository followRepository;
    @Mock UserRepository userRepository;
    @InjectMocks FollowService followService;

    @Test
    void followCreatesRelationship() {
        User follower = User.builder().id(UUID.randomUUID()).build();
        User following = User.builder().id(UUID.randomUUID()).username("target").build();
        when(userRepository.findByUsernameIgnoreCase("target")).thenReturn(Optional.of(following));

        followService.follow(follower, "target");

        ArgumentCaptor<Follow> captor = ArgumentCaptor.forClass(Follow.class);
        verify(followRepository).save(captor.capture());
        assertSame(follower, captor.getValue().getFollower());
        assertSame(following, captor.getValue().getFollowing());
        assertEquals(FollowStatus.ACCEPTED, captor.getValue().getStatus());
    }

    @Test
    void privateProfileCreatesPendingRequest() {
        User follower = User.builder().id(UUID.randomUUID()).build();
        User following = User.builder()
                .id(UUID.randomUUID())
                .username("private-user")
                .privacyStatus(true)
                .build();
        when(userRepository.findByUsernameIgnoreCase("private-user"))
                .thenReturn(Optional.of(following));

        followService.follow(follower, "private-user");

        ArgumentCaptor<Follow> captor = ArgumentCaptor.forClass(Follow.class);
        verify(followRepository).save(captor.capture());
        assertEquals(FollowStatus.PENDING, captor.getValue().getStatus());
    }

    @Test
    void userCannotFollowOwnProfile() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("felipe").build();
        when(userRepository.findByUsernameIgnoreCase("felipe")).thenReturn(Optional.of(user));

        assertThrows(InvalidFollowException.class, () -> followService.follow(user, "felipe"));
        verify(followRepository, never()).save(any());
    }

    @Test
    void duplicateFollowIsRejected() {
        User follower = User.builder().id(UUID.randomUUID()).build();
        User following = User.builder().id(UUID.randomUUID()).username("target").build();
        when(userRepository.findByUsernameIgnoreCase("target")).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerIdAndFollowingId(
                follower.getId(),
                following.getId()
        )).thenReturn(true);

        assertThrows(
                FollowAlreadyExistsException.class,
                () -> followService.follow(follower, "target")
        );
        verify(followRepository, never()).save(any());
    }

    @Test
    void profileOwnerCanAcceptPendingRequest() {
        User follower = User.builder().id(UUID.randomUUID()).build();
        User owner = User.builder().id(UUID.randomUUID()).build();
        UUID requestId = UUID.randomUUID();
        Follow request = new Follow(
                requestId,
                follower,
                owner,
                Instant.now(),
                FollowStatus.PENDING
        );
        when(followRepository.findByIdAndFollowingId(requestId, owner.getId()))
                .thenReturn(Optional.of(request));

        followService.accept(owner, requestId);

        assertEquals(FollowStatus.ACCEPTED, request.getStatus());
        verify(followRepository).save(request);
    }

    @Test
    void relationshipReturnsFollowingForAcceptedRelationship() {
        User follower = User.builder().id(UUID.randomUUID()).build();
        User following = User.builder().id(UUID.randomUUID()).username("target").build();
        Follow relationship = new Follow(
                UUID.randomUUID(),
                follower,
                following,
                Instant.now(),
                FollowStatus.ACCEPTED
        );
        when(userRepository.findByUsernameIgnoreCase("target")).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerIdAndFollowingId(follower.getId(), following.getId()))
                .thenReturn(Optional.of(relationship));

        assertEquals(
                FollowRelationshipStatus.FOLLOWING,
                followService.relationship(follower, "target").status()
        );
    }

    @Test
    void relationshipReturnsPendingForPendingRequest() {
        User follower = User.builder().id(UUID.randomUUID()).build();
        User following = User.builder().id(UUID.randomUUID()).username("target").build();
        Follow relationship = new Follow(
                UUID.randomUUID(),
                follower,
                following,
                Instant.now(),
                FollowStatus.PENDING
        );
        when(userRepository.findByUsernameIgnoreCase("target")).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerIdAndFollowingId(follower.getId(), following.getId()))
                .thenReturn(Optional.of(relationship));

        assertEquals(
                FollowRelationshipStatus.PENDING,
                followService.relationship(follower, "target").status()
        );
    }

    @Test
    void relationshipReturnsNotFollowingWhenRelationshipDoesNotExist() {
        User follower = User.builder().id(UUID.randomUUID()).build();
        User following = User.builder().id(UUID.randomUUID()).username("target").build();
        when(userRepository.findByUsernameIgnoreCase("target")).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerIdAndFollowingId(follower.getId(), following.getId()))
                .thenReturn(Optional.empty());

        assertEquals(
                FollowRelationshipStatus.NOT_FOLLOWING,
                followService.relationship(follower, "target").status()
        );
    }
}
