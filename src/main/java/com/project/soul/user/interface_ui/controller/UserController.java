package com.project.soul.user.interface_ui.controller;

import com.project.soul.user.domain.entity.User;
import com.project.soul.user.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    //criar usuario
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User newUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.listUser();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user){

        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(RequestBody LoginRequestDTO loginDTO) {
        try {
            User userLogado = userService.realizarLogin(loginDTO);
            return ResponseEntity.ok(userLogado); // Retorna 200 OK com os dados do usuário
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // Retorna 401 se errar
        }
    }
}
