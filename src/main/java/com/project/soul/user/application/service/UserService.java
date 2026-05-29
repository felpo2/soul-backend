package com.project.soul.user.application.service;

import com.project.soul.user.application.dto.LoginRequestDTO;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //CRIAR USUARIO
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Este e-mail já está em uso!");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Este nome de usuário já está em uso!");
        }

        user.setCreatedAt(new Date());
        user.setAccountStatus(true);

        return userRepository.save(user);
    }

    // LISTAR USUARIOS
    public List<User> listUser() {
        return userRepository.findAll();
    }

    //ATUALIZAR USUARIO
    public User updateUser(Long id, User updatedUser) {
        User existentUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        existentUser.setName(updatedUser.getName());
        existentUser.setBio(updatedUser.getBio());
        existentUser.setProfilePicture(updatedUser.getProfilePicture());

        return userRepository.save(existentUser);
    }

    // DELETAR USUARIO
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // LOGAR
    public User realizeLogin(LoginRequestDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new RuntimeException("E-mail not registered."));

        if (!user.getPassword().equals(loginDTO.password())) {
            throw new RuntimeException("Invalid password.");
        }
        return user;
    }
}