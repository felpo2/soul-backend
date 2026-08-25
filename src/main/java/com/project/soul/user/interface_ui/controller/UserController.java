package com.project.soul.user.interface_ui.controller;

import com.project.soul.user.application.dto.*;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.application.service.RefreshTokenService;
import com.project.soul.user.application.service.ProfileService;
import com.project.soul.user.application.service.UserService;
import com.project.soul.user.application.service.FollowService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    ProfileService profileService;

    @Autowired
    FollowService followService;

    //criar usuario
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequestDTO request) {
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

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponseDTO> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(CurrentUserResponseDTO.from(authenticatedUser(authentication)));
    }

    @PutMapping("/me")
    public ResponseEntity<CurrentUserResponseDTO> updateCurrentUser(
            @Valid @RequestBody UpdateUserRequestDTO request,
            Authentication authentication) {
        User user = authenticatedUser(authentication);
        User updatedUser = userService.updateUser(user.getId(), request);
        return ResponseEntity.ok(CurrentUserResponseDTO.from(updatedUser));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(Authentication authentication) {
        User user = authenticatedUser(authentication);
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> changeCurrentUserPassword(
            @Valid @RequestBody ChangePasswordDTO dto,
            Authentication authentication) {
        User user = authenticatedUser(authentication);
        userService.changePassword(user.getId(), dto);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PutMapping("/me/email")
    public ResponseEntity<String> changeCurrentUserEmail(
            @Valid @RequestBody ChangeEmailDTO dto,
            Authentication authentication) {
        User user = authenticatedUser(authentication);
        userService.changeEmail(user.getId(), dto);
        return ResponseEntity.ok("E-mail changed successfully");
    }

    @PutMapping("/me/privacy")
    public ResponseEntity<PublicProfileResponseDTO> updateCurrentUserPrivacy(
            @Valid @RequestBody ProfileVisibilityDTO request,
            Authentication authentication) {
        User user = authenticatedUser(authentication);
        return ResponseEntity.ok(
                profileService.updateVisibility(user, request.privateProfile())
        );
    }

    @GetMapping("/me/follow-requests")
    public ResponseEntity<Page<FollowRequestResponseDTO>> followRequests(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            Authentication authentication) {
        User user = authenticatedUser(authentication);
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );
        return ResponseEntity.ok(followService.pendingRequests(user, pageable));
    }

    @PostMapping("/me/follow-requests/{requestId}/accept")
    public ResponseEntity<Void> acceptFollowRequest(
            @PathVariable UUID requestId,
            Authentication authentication) {
        followService.accept(authenticatedUser(authentication), requestId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/follow-requests/{requestId}")
    public ResponseEntity<Void> rejectFollowRequest(
            @PathVariable UUID requestId,
            Authentication authentication) {
        followService.reject(authenticatedUser(authentication), requestId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequestDTO request,
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        try {
            LoginResponseDTO response = userService.realizeLogin(dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    //ENVIO DO E-MAIL DE RECUPERAÇÃO (ainda incompleto/não funcionando)
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestParam @NotBlank @Email String email) {
        userService.requestPasswordReset(email);
        return ResponseEntity.ok("If the email is registered, the recovery token has been sent!");
    }


    //RESET PASSWORD PARA ESQUECI A SENHA
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam @NotBlank String token,
            @RequestParam @NotBlank @Size(min = 8, max = 72) String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password changed successfully!");
    }

    //mudar senha PARA ALTERAÇÃO DA SENHA DO PERFIL(OBS: não é ESQUECI A SENHA)
    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordDTO dto,
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
            @Valid @RequestBody ChangeEmailDTO dto,
            Authentication authentication) {
        ensureOwnAccount(id, authentication);
        try {
            userService.changeEmail(id, dto);
            return ResponseEntity.ok("E-mail changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(refreshTokenService.rotate(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        refreshTokenService.revoke(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    private void ensureOwnAccount(UUID requestedId, Authentication authentication) {
        User authenticatedUser = authenticatedUser(authentication);
        if (!authenticatedUser.getId().equals(requestedId)) {
            throw new AccessDeniedException("You cannot modify another user's account.");
        }
    }

    private User authenticatedUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
