package com.project.soul.user.interface_ui.controller;

import com.project.soul.user.application.dto.ChangeEmailDTO;
import com.project.soul.user.application.dto.ChangePasswordDTO;
import com.project.soul.user.application.dto.LoginRequestDTO;
import com.project.soul.user.application.dto.LoginResponseDTO;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody User user){

        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        try {
            LoginResponseDTO response = userService.realizeLogin(dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    //ENVIO DO E-MAIL DE RECUPERAÇÃO (ainda incompleto/não funcionando)
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.requestPasswordReset(email);
        return ResponseEntity.ok("If the email is registered, the recovery token has been sent!");
    }


    //RESET PASSWORD PARA ESQUECI A SENHA
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password changed successfully!");
    }

    //mudar senha PARA ALTERAÇÃO DA SENHA DO PERFIL(OBS: não é ESQUECI A SENHA)
    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordDTO dto) {
        try {
            userService.changePassword(id, dto);
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //Mudar e-mail
    @PutMapping("/{id}/change-email")
    public ResponseEntity<String> changeEmail(
            @PathVariable Long id,
            @RequestBody ChangeEmailDTO dto) {
        try {
            userService.changeEmail(id, dto);
            return ResponseEntity.ok("E-mail changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
