package com.project.soul.posts.interface_ui.controller;

import com.project.soul.posts.application.dto.PostResponseDTO;
import com.project.soul.posts.application.dto.PostRequestDTO;
import com.project.soul.posts.application.service.PostService;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.application.service.ProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
@Validated
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ProfileService profileService;

    // Criar publicação
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @Valid @RequestBody PostRequestDTO post,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        PostResponseDTO newPost = postService.createPost(user, post);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newPost);
    }

    // Listar todos os posts
    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> listPosts(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                postService.listPosts(pageable)
        );
    }

    // Listar posts de um usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponseDTO>> listPostsByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            Authentication authentication) {

        User viewer = (User) authentication.getPrincipal();
        profileService.ensureCanViewPosts(userId, viewer);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                postService.listPostsByUser(userId, pageable)
        );


    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable UUID postId,
            @Valid @RequestBody PostRequestDTO updatedPost,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        PostResponseDTO post = postService.updatePost(
                postId,
                user,
                updatedPost
        );

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID postId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        postService.deletePost(postId, user);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(
            @PathVariable UUID postId) {

        return ResponseEntity.ok(
                postService.getPostById(postId)
        );
    }
}
