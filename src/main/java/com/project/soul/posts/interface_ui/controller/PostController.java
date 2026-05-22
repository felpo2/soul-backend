package com.project.soul.interface_ui.controller;

import com.project.soul.domain.entity.Post;
import com.project.soul.application.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    //criar publicacoes
    @PostMapping("/user/{userId}")
    public ResponseEntity<Post> createPost(@PathVariable Long userId, @RequestBody Post post){
        Post newPost = postService.createPost(userId, post);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
    }
}