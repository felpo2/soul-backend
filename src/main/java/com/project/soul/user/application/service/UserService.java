package com.project.soul.user.application.service;

import com.project.soul.user.application.dto.ChangeEmailDTO;
import com.project.soul.user.application.dto.ChangePasswordDTO;
import com.project.soul.user.application.dto.LoginRequestDTO;
import com.project.soul.user.application.dto.LoginResponseDTO;
import com.project.soul.user.application.dto.UpdateUserRequestDTO;
import com.project.soul.user.domain.entity.PasswordResetToken;
import com.project.soul.user.domain.entity.User;
import com.project.soul.user.domain.repository.TokenRepository;
import com.project.soul.user.domain.repository.UserRepository;
import com.project.soul.user.infrastructure.security.JwtTokenService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private TokenRepository tokenRepository;
    private EmailService emailService;
    private JwtTokenService jwtTokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenRepository tokenRepository, EmailService emailService, JwtTokenService jwtTokenService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.jwtTokenService = jwtTokenService;
    }

    //CRIAR USUARIO
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("This email is already in use!");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("This username is already in use!");
        }

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        user.setCreatedAt(new Date());
        user.setAccountStatus(true);

        return userRepository.save(user);
    }

    // LISTAR USUARIOS
    public List<User> listUser() {
        return userRepository.findAll();
    }

    //ATUALIZAR USUARIO
    public User updateUser(UUID id, UpdateUserRequestDTO updatedUser) {
        User existentUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        existentUser.setName(updatedUser.name());
        existentUser.setBio(updatedUser.bio());
        existentUser.setProfilePicture(updatedUser.profilePicture());


        return userRepository.save(existentUser);
    }

    // DELETAR USUARIO
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // LOGAR
    public LoginResponseDTO realizeLogin(LoginRequestDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new RuntimeException("Email not registered."));

        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password.");
        }

        // Gera o token JWT
        String token = jwtTokenService.generateToken(user);
        return new LoginResponseDTO(token);
    }

    //Envio do email para resetar senha
    @Transactional
    public void requestPasswordReset(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()){
            return;
        }

       User user = userOptional.get();

        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                user,
                Instant.now().plus(15, ChronoUnit.MINUTES)
        );


        tokenRepository.save(resetToken);

        emailService.sendResetPasswordEmail(user.getEmail(), token);

    }

    //Resetar senha
    @Transactional
    public void resetPassword(String token, String newPassword){
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(()-> new RuntimeException("Invalid or non-existent token."));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token expired. Request a new recovery.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }

    //Metodo ordinario para mudar senha
    public void changePassword(UUID userId, ChangePasswordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new RuntimeException("The current password is incorrect!");
        }

        if (passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new RuntimeException("The new password must be different from the current password.");
        }

        //Criptografa e salva a nova senha
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    public void changeEmail(UUID userId, ChangeEmailDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect password! Unable to change the email.");
        }

        if (userRepository.existsByEmail(dto.newEmail())) {
            throw new RuntimeException("This email is already in use by another account.");
        }

        user.setEmail(dto.newEmail());
        userRepository.save(user);
    }



}
