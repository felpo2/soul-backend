package com.project.soul.service;

import com.project.soul.entity.Post;
import com.project.soul.entity.User;
import com.project.soul.repository.PostRepository;
import com.project.soul.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    //metodo criar postagem
    public Post createPost(Long userId, Post post){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        post.setUser(user);
        return postRepository.save(post);
    }
}
