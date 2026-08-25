package com.project.soul.user.application.service;

import com.project.soul.posts.domain.repository.PostRepository;
import com.project.soul.user.application.dto.ProfileSummaryDTO;
import com.project.soul.user.application.dto.PublicProfileResponseDTO;
import com.project.soul.user.application.exception.UserNotFoundException;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.repository.UserRepository;
import com.project.soul.user.domain.repository.FollowRepository;
import com.project.soul.user.domain.entity.FollowStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    public ProfileService(
            UserRepository userRepository,
            PostRepository postRepository,
            FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followRepository = followRepository;
    }

    public Page<ProfileSummaryDTO> search(String query, Pageable pageable) {
        return userRepository
                .findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
                        query.trim(),
                        query.trim(),
                        pageable
                )
                .map(ProfileSummaryDTO::from);
    }

    public PublicProfileResponseDTO getByUsername(String username) {
        User user = findByUsername(username);
        return toResponse(user);
    }

    @Transactional
    public PublicProfileResponseDTO updateVisibility(User user, boolean privateProfile) {
        user.setPrivacyStatus(privateProfile);
        User updated = userRepository.save(user);
        return toResponse(updated);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    public void ensureCanViewPosts(UUID profileId, User viewer) {
        User profile = userRepository.findById(profileId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        boolean owner = profile.getId().equals(viewer.getId());
        boolean follower = !owner && followRepository.existsAcceptedRelationship(
                viewer.getId(),
                profile.getId(),
                FollowStatus.ACCEPTED
        );
        if (Boolean.TRUE.equals(profile.getPrivacyStatus()) && !owner && !follower) {
            throw new AccessDeniedException("This profile is private.");
        }
    }

    private PublicProfileResponseDTO toResponse(User user) {
        return PublicProfileResponseDTO.from(
                user,
                postRepository.countByUserId(user.getId()),
                followRepository.countAcceptedFollowers(user.getId(), FollowStatus.ACCEPTED),
                followRepository.countAcceptedFollowing(user.getId(), FollowStatus.ACCEPTED)
        );
    }
}
