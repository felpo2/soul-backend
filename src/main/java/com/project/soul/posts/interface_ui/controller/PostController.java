package com.project.soul.posts.interface_ui.controller;

import com.project.soul.posts.application.dto.PostResponseDTO;
import com.project.soul.posts.domain.entity.Post;
import com.project.soul.posts.application.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // Criar publicação
    @PostMapping("/user/{userId}")
    public ResponseEntity<Post> createPost(
            @PathVariable UUID userId,
            @RequestBody Post post) {

        Post newPost = postService.createPost(userId, post);

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
}