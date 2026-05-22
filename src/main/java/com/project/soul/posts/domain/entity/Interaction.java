package com.project.soul.domain.entity;

import jakarta.persistence.*;

import java.util.Date;

public class Interaction {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private String type;
    private String content;
    private Date createdAt;
}
