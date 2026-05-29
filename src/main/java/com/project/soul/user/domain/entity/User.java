package com.project.soul.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "tb_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private Date dateOfBirth;
    private String profilePicture;
    private String bio;
    private Date createdAt;
    private Boolean accountStatus;
    private Boolean privacyStatus;
    private Boolean metricsStatus;

}
