package com.project.soul.user.application.service;

import com.project.soul.user.application.dto.ProfileSummaryDTO;
import com.project.soul.user.application.dto.FollowRequestResponseDTO;
import com.project.soul.user.application.dto.FollowRelationshipResponseDTO;
import com.project.soul.user.application.dto.FollowRelationshipStatus;
import com.project.soul.user.application.exception.FollowAlreadyExistsException;
import com.project.soul.user.application.exception.InvalidFollowException;
import com.project.soul.user.application.exception.UserNotFoundException;
import com.project.soul.user.domain.entity.Follow;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.entity.FollowStatus;
import com.project.soul.user.domain.repository.FollowRepository;
import com.project.soul.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void follow(User follower, String username) {
        User following = findUser(username);
        if (follower.getId().equals(following.getId())) {
            throw new InvalidFollowException("A user cannot follow their own profile.");
        }
        if (followRepository.existsByFollowerIdAndFollowingId(
                follower.getId(),
                following.getId()
        )) {
            throw new FollowAlreadyExistsException("User is already following this profile.");
        }
        FollowStatus status = Boolean.TRUE.equals(following.getPrivacyStatus())
                ? FollowStatus.PENDING
                : FollowStatus.ACCEPTED;
        followRepository.save(new Follow(follower, following, Instant.now(), status));
    }

    @Transactional
    public void unfollow(User follower, String username) {
        User following = findUser(username);
        followRepository.deleteByFollowerIdAndFollowingId(
                follower.getId(),
                following.getId()
        );
    }

    public Page<ProfileSummaryDTO> followers(String username, Pageable pageable) {
        User user = findUser(username);
        return followRepository.findAcceptedFollowers(
                        user.getId(),
                        FollowStatus.ACCEPTED,
                        pageable
                )
                .map(follow -> ProfileSummaryDTO.from(follow.getFollower()));
    }

    public Page<ProfileSummaryDTO> following(String username, Pageable pageable) {
        User user = findUser(username);
        return followRepository.findAcceptedFollowing(
                        user.getId(),
                        FollowStatus.ACCEPTED,
                        pageable
                )
                .map(follow -> ProfileSummaryDTO.from(follow.getFollowing()));
    }

    public Page<FollowRequestResponseDTO> pendingRequests(User user, Pageable pageable) {
        return followRepository.findByFollowingIdAndStatus(
                        user.getId(),
                        FollowStatus.PENDING,
                        pageable
                )
                .map(FollowRequestResponseDTO::from);
    }

    public FollowRelationshipResponseDTO relationship(User follower, String username) {
        User following = findUser(username);
        if (follower.getId().equals(following.getId())) {
            return new FollowRelationshipResponseDTO(FollowRelationshipStatus.NOT_FOLLOWING);
        }
        return followRepository.findByFollowerIdAndFollowingId(
                        follower.getId(),
                        following.getId()
                )
                .map(follow -> new FollowRelationshipResponseDTO(
                        follow.getStatus() == FollowStatus.PENDING
                                ? FollowRelationshipStatus.PENDING
                                : FollowRelationshipStatus.FOLLOWING
                ))
                .orElseGet(() -> new FollowRelationshipResponseDTO(
                        FollowRelationshipStatus.NOT_FOLLOWING
                ));
    }

    @Transactional
    public void accept(User user, java.util.UUID requestId) {
        Follow follow = findOwnedRequest(user, requestId);
        if (follow.getStatus() != FollowStatus.PENDING) {
            throw new InvalidFollowException("Follow request is not pending.");
        }
        follow.accept();
        followRepository.save(follow);
    }

    @Transactional
    public void reject(User user, java.util.UUID requestId) {
        Follow follow = findOwnedRequest(user, requestId);
        followRepository.delete(follow);
    }

    private Follow findOwnedRequest(User user, java.util.UUID requestId) {
        return followRepository.findByIdAndFollowingId(requestId, user.getId())
                .orElseThrow(() -> new UserNotFoundException("Follow request not found."));
    }

    private User findUser(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }
}
