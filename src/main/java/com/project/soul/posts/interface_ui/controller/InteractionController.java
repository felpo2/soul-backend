package com.project.soul.posts.interface_ui.controller;

import com.project.soul.posts.application.service.InteractionService;
import com.project.soul.user.domain.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        interactionService.likePost(postId, user.getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable UUID postId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        interactionService.unlikePost(postId, user.getId());

        return ResponseEntity.noContent().build();
    }
}
