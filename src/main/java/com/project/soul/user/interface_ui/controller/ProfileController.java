package com.project.soul.user.interface_ui.controller;

import com.project.soul.posts.application.dto.PostResponseDTO;
import com.project.soul.posts.application.service.PostService;
import com.project.soul.user.application.dto.ProfileSummaryDTO;
import com.project.soul.user.application.dto.PublicProfileResponseDTO;
import com.project.soul.user.application.dto.FollowRelationshipResponseDTO;
import com.project.soul.user.application.service.ProfileService;
import com.project.soul.user.application.service.FollowService;
import com.project.soul.user.domain.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@Validated
public class ProfileController {

    private final ProfileService profileService;
    private final PostService postService;
    private final FollowService followService;

    public ProfileController(
            ProfileService profileService,
            PostService postService,
            FollowService followService) {
        this.profileService = profileService;
        this.postService = postService;
        this.followService = followService;
    }

    @GetMapping
    public ResponseEntity<Page<ProfileSummaryDTO>> search(
            @RequestParam @NotBlank @Size(max = 100) String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        return ResponseEntity.ok(profileService.search(query, pageable));
    }

    @GetMapping("/{username}")
    public ResponseEntity<PublicProfileResponseDTO> getByUsername(
            @PathVariable String username) {
        return ResponseEntity.ok(profileService.getByUsername(username));
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<Page<PostResponseDTO>> getPosts(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            Authentication authentication) {
        User viewer = (User) authentication.getPrincipal();
        User profile = profileService.findByUsername(username);
        profileService.ensureCanViewPosts(profile.getId(), viewer);
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return ResponseEntity.ok(postService.listPostsByUser(profile.getId(), pageable));
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<Void> follow(
            @PathVariable String username,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        followService.follow(user, username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<Void> unfollow(
            @PathVariable String username,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        followService.unfollow(user, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/follow-status")
    public ResponseEntity<FollowRelationshipResponseDTO> followStatus(
            @PathVariable String username,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(followService.relationship(user, username));
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<Page<ProfileSummaryDTO>> followers(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(followService.followers(username, pageable));
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<Page<ProfileSummaryDTO>> following(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(followService.following(username, pageable));
    }
}
