package com.project.soul.posts.application.service;

import com.project.soul.posts.application.dto.PostResponseDTO;
import com.project.soul.posts.application.dto.PostRequestDTO;
import com.project.soul.posts.application.exception.PostNotFoundException;
import com.project.soul.posts.domain.entity.Post;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.entity.FollowStatus;
import com.project.soul.posts.domain.repository.PostRepository;
import com.project.soul.posts.domain.repository.CommentRepository;
import com.project.soul.posts.domain.repository.InteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private InteractionRepository interactionRepository;

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

    @Transactional
    public void deletePost(UUID postId, User authenticatedUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found."));

        if (!post.getUser().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this post.");
        }

        commentRepository.deleteByPostId(postId);
        interactionRepository.deleteByPostId(postId);
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

    public Page<PostResponseDTO> listFeed(User user, Pageable pageable) {
        return postRepository.findFeed(user.getId(), FollowStatus.ACCEPTED, pageable)
                .map(this::toResponse);
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

