package com.project.soul.user.interface_ui.controller;

import com.project.soul.user.application.dto.*;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequestDTO request) {
        try {
            User user = User.builder()
                    .name(request.name())
                    .username(request.username())
                    .email(request.email())
                    .password(request.password())
                    .dateOfBirth(request.dateOfBirth())
                    .profilePicture(request.profilePicture())
                    .bio(request.bio())
                    .build();
            User newUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDTO.from(newUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(){
        List<UserResponseDTO> users = userService.listUser().stream()
                .map(UserResponseDTO::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequestDTO request,
            Authentication authentication) {
        ensureOwnAccount(id, authentication);
        User updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(UserResponseDTO.from(updatedUser));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id, Authentication authentication){
        ensureOwnAccount(id, authentication);
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
            @PathVariable UUID id,
            @RequestBody ChangePasswordDTO dto,
            Authentication authentication) {
        ensureOwnAccount(id, authentication);
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
            @PathVariable UUID id,
            @RequestBody ChangeEmailDTO dto,
            Authentication authentication) {
        ensureOwnAccount(id, authentication);
        try {
            userService.changeEmail(id, dto);
            return ResponseEntity.ok("E-mail changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private void ensureOwnAccount(UUID requestedId, Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();
        if (!authenticatedUser.getId().equals(requestedId)) {
            throw new AccessDeniedException("You cannot modify another user's account.");
        }
    }
}
