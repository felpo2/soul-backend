package com.project.soul.user.application.service;

import com.project.soul.posts.domain.repository.PostRepository;
import com.project.soul.user.application.dto.ProfileSummaryDTO;
import com.project.soul.user.application.dto.PublicProfileResponseDTO;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.repository.UserRepository;
import com.project.soul.user.domain.repository.FollowRepository;
import com.project.soul.user.domain.entity.FollowStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock UserRepository userRepository;
    @Mock PostRepository postRepository;
    @Mock FollowRepository followRepository;
    @InjectMocks ProfileService profileService;

    @Test
    void searchReturnsOnlyPublicProfileFields() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Felipe")
                .username("felipe")
                .email("private@example.com")
                .password("password-hash")
                .build();
        PageRequest pageable = PageRequest.of(0, 20);
        when(userRepository.findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
                "felipe", "felipe", pageable
        )).thenReturn(new PageImpl<>(List.of(user)));

        Page<ProfileSummaryDTO> result = profileService.search(" felipe ", pageable);

        assertEquals("felipe", result.getContent().getFirst().username());
    }

    @Test
    void publicProfileIncludesPostCount() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("felipe").build();
        when(userRepository.findByUsernameIgnoreCase("Felipe")).thenReturn(Optional.of(user));
        when(postRepository.countByUserId(userId)).thenReturn(7L);
        when(followRepository.countAcceptedFollowers(userId, FollowStatus.ACCEPTED)).thenReturn(3L);
        when(followRepository.countAcceptedFollowing(userId, FollowStatus.ACCEPTED)).thenReturn(5L);

        PublicProfileResponseDTO response = profileService.getByUsername("Felipe");

        assertEquals(7L, response.postCount());
        assertEquals(3L, response.followerCount());
        assertEquals(5L, response.followingCount());
        assertEquals("felipe", response.username());
    }

    @Test
    void privateProfileBlocksAnotherUserFromViewingPosts() {
        UUID profileId = UUID.randomUUID();
        User profile = User.builder().id(profileId).privacyStatus(true).build();
        User viewer = User.builder().id(UUID.randomUUID()).build();
        when(userRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(followRepository.existsAcceptedRelationship(
                viewer.getId(),
                profileId,
                FollowStatus.ACCEPTED
        ))
                .thenReturn(false);

        assertThrows(
                AccessDeniedException.class,
                () -> profileService.ensureCanViewPosts(profileId, viewer)
        );
    }

    @Test
    void ownerCanViewOwnPrivatePosts() {
        UUID profileId = UUID.randomUUID();
        User profile = User.builder().id(profileId).privacyStatus(true).build();
        when(userRepository.findById(profileId)).thenReturn(Optional.of(profile));

        assertDoesNotThrow(() -> profileService.ensureCanViewPosts(profileId, profile));
    }
}
