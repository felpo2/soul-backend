package com.project.soul.application.service;

import com.project.soul.domain.entity.User;
import com.project.soul.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    //metodo criar usuario
    @PostMapping()
    public User createUser(User user){
        user.setCreatedAt(new Date());
        user.setAccountStatus(true);

        return userRepository.save(user);
    }

    //listar usuario
    public List<User> listUser(){
        return userRepository.findAll();
    }

    //atualizar usuario
    public User updateUser(Long id, User updatedUser){
        User existentUser = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found with ID: "+id));

        existentUser.setName(updatedUser.getName());
        existentUser.setBio(updatedUser.getBio());
        existentUser.setProfilePicture(updatedUser.getProfilePicture());

        return userRepository.save(existentUser);
    }

    //deletar usuario
    public void deleteUser(Long id){
        if (!userRepository.existsById(id)){
            throw new RuntimeException("Usuário não encontrado com o ID: "+id);
        }
        userRepository.deleteById(id);
    }




}
