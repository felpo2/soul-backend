package com.project.soul.posts.application.service;

import com.project.soul.posts.application.dto.PostResponseDTO;
import com.project.soul.posts.application.dto.PostRequestDTO;
import com.project.soul.posts.application.exception.PostNotFoundException;
import com.project.soul.posts.domain.entity.Post;
import com.project.soul.user.domain.entity.User;
import com.project.soul.posts.domain.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // Criar postagem
    public PostResponseDTO createPost(User user, PostRequestDTO request) {
        Post post = new Post();
        post.setContent(request.content());
        post.setImageUrl(request.imageUrl());
        post.setUser(user);
        post.setCreatedAt(new Date());
        return toResponse(postRepository.save(post));
    }

    // Listar todos os posts
    public Page<PostResponseDTO> listPosts(Pageable pageable) {

        return postRepository.findAll(pageable)
                .map(post -> new PostResponseDTO(
                        post.getId(),
                        post.getContent(),
                        post.getImageUrl(),
                        post.getCreatedAt(),
                        post.getUser().getId(),
                        post.getUser().getUsername(),
                        post.getUser().getName(),
                        post.getUser().getProfilePicture()
                ));
    }

    // Listar posts de um usuário
    public Page<PostResponseDTO> listPostsByUser(
            UUID userId,
            Pageable pageable) {

        return postRepository.findByUserId(userId, pageable)
                .map(post -> new PostResponseDTO(
                        post.getId(),
                        post.getContent(),
                        post.getImageUrl(),
                        post.getCreatedAt(),
                        post.getUser().getId(),
                        post.getUser().getUsername(),
                        post.getUser().getName(),
                        post.getUser().getProfilePicture()
                ));
    }

    public PostResponseDTO updatePost(UUID postId, User authenticatedUser, PostRequestDTO updatedPost) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found."));

        if (!post.getUser().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("You are not allowed to edit this post.");
        }

        post.setContent(updatedPost.content());
        post.setImageUrl(updatedPost.imageUrl());

        return toResponse(postRepository.save(post));
    }

    public void deletePost(UUID postId, User authenticatedUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found."));

        if (!post.getUser().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this post.");
        }

        postRepository.delete(post);
    }

    public PostResponseDTO getPostById(UUID postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found."));

        return new PostResponseDTO(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                post.getCreatedAt(),
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getUser().getName(),
                post.getUser().getProfilePicture()
        );
    }

    private PostResponseDTO toResponse(Post post) {
        return new PostResponseDTO(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                post.getCreatedAt(),
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getUser().getName(),
                post.getUser().getProfilePicture()
        );
    }
}

