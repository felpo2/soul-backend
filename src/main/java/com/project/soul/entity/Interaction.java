package com.project.soul.entity;

import jakarta.persistence.*;

import java.util.Date;

public class Interaction {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    String type;
    String content;
    Date createdAt;
}
