package com.project.soul.posts.application.service;

import com.project.soul.posts.application.dto.CommentRequestDTO;
import com.project.soul.posts.application.dto.CommentResponseDTO;
import com.project.soul.posts.application.exception.PostNotFoundException;
import com.project.soul.posts.domain.entity.Comment;
import com.project.soul.posts.domain.entity.Post;
import com.project.soul.posts.domain.repository.CommentRepository;
import com.project.soul.posts.domain.repository.PostRepository;
import com.project.soul.user.domain.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public CommentResponseDTO create(UUID postId, User user, CommentRequestDTO request) {
        Post post = findPost(postId);
        Comment comment = new Comment(user, post, request.content(), Instant.now());
        return CommentResponseDTO.from(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponseDTO update(
            UUID postId,
            UUID commentId,
            User user,
            CommentRequestDTO request) {
        Comment comment = findComment(postId, commentId);
        ensureOwner(comment, user);
        comment.updateContent(request.content());
        return CommentResponseDTO.from(commentRepository.save(comment));
    }

    @Transactional
    public void delete(UUID postId, UUID commentId, User user) {
        Comment comment = findComment(postId, commentId);
        ensureOwner(comment, user);
        commentRepository.delete(comment);
    }

    @Transactional
    public Page<CommentResponseDTO> list(UUID postId, Pageable pageable) {
        findPost(postId);
        return commentRepository.findByPostId(postId, pageable).map(CommentResponseDTO::from);
    }

    public long count(UUID postId) {
        findPost(postId);
        return commentRepository.countByPostId(postId);
    }

    private Post findPost(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found."));
    }

    private Comment findComment(UUID postId, UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostNotFoundException("Comment not found."));
        if (!comment.getPost().getId().equals(postId)) {
            throw new PostNotFoundException("Comment not found.");
        }
        return comment;
    }

    private void ensureOwner(Comment comment, User user) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to modify this comment.");
        }
    }
}
