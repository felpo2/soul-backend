package com.project.soul.user.domain.repository;

import com.project.soul.user.domain.entity.PasswordResetToken;
import com.project.soul.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


//REPOSITÓRIO DO TOKEN DE RECUPERAÇÃO DE SENHA, NÃO DE REQUISIÇÕES HTTP (JWT)
@Repository
public interface TokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);
}
