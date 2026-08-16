package com.project.soul.posts.interface_ui.controller;

import com.project.soul.posts.application.service.InteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable UUID postId,
            @RequestAttribute("userId") UUID userId
    ) {

        interactionService.likePost(postId, userId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable UUID postId,
            @RequestAttribute("userId") UUID userId
    ) {

        interactionService.unlikePost(postId, userId);

        return ResponseEntity.noContent().build();
    }
}