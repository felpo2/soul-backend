package com.project.soul.posts.interface_ui.controller;

import com.project.soul.posts.application.dto.CommentRequestDTO;
import com.project.soul.posts.application.dto.CommentResponseDTO;
import com.project.soul.posts.application.service.CommentService;
import com.project.soul.user.domain.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/posts/{postId}/comments")
@Validated
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDTO> create(
            @PathVariable UUID postId,
            @Valid @RequestBody CommentRequestDTO request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.create(postId, user, request));
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponseDTO>> list(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "createdAt")
        );
        return ResponseEntity.ok(commentService.list(postId, pageable));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> update(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @Valid @RequestBody CommentRequestDTO request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(commentService.update(postId, commentId, user, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        commentService.delete(postId, commentId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> count(@PathVariable UUID postId) {
        return ResponseEntity.ok(Map.of("comments", commentService.count(postId)));
    }
}
