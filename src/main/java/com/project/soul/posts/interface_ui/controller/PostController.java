package com.project.soul.posts.interface_ui.controller;

import com.project.soul.posts.application.dto.PostResponseDTO;
import com.project.soul.posts.application.service.PostService;
import com.project.soul.posts.domain.entity.Post;
import com.project.soul.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // Criar publicação
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestBody Post post,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Post newPost = postService.createPost(user, post);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newPost);
    }

    // Listar todos os posts
    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

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
    public ResponseEntity<Post> updatePost(
            @PathVariable UUID postId,
            @RequestBody Post updatedPost,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Post post = postService.updatePost(
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