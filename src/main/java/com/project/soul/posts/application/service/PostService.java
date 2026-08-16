package com.project.soul.posts.application.service;

import com.project.soul.posts.application.dto.PostResponseDTO;
import com.project.soul.posts.domain.entity.Post;
import com.project.soul.user.domain.entity.User;
import com.project.soul.posts.domain.repository.PostRepository;
import com.project.soul.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    // Criar postagem
    public Post createPost(User user, Post post) {

        post.setUser(user);
        post.setCreatedAt(new Date());

        return postRepository.save(post);
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

    public Post updatePost(UUID postId, User authenticatedUser, Post updatedPost) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found."));

        if (!post.getUser().getId().equals(authenticatedUser.getId())) {
            throw new RuntimeException("You are not allowed to edit this post.");
        }

        post.setContent(updatedPost.getContent());

        return postRepository.save(post);
    }

    public void deletePost(UUID postId, User authenticatedUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found."));

        if (!post.getUser().getId().equals(authenticatedUser.getId())) {
            throw new RuntimeException("You are not allowed to delete this post.");
        }

        postRepository.delete(post);
    }
}

