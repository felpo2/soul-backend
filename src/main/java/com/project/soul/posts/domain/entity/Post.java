package com.project.soul.posts.domain.entity;

import com.project.soul.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String content;
    private String imageUrl;
    private Date createdAt;
    private Boolean visibility;
    private Boolean metricsStatus;

}
