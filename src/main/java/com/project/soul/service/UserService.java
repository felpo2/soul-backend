package com.project.soul.service;

import com.project.soul.entity.User;
import com.project.soul.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    UserRepository usuarioRepository;

    //metodo criar usuario
    @PostMapping()
    public User createUser(User user){
        user.setCreatedAt(new Date());
        user.setAccountStatus(true);

        return usuarioRepository.save(user);
    }





}
