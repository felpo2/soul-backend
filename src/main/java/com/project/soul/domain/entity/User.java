package com.project.soul.domain.entity;

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
    private String username;
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
