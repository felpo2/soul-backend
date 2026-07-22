package com.project.soul.user.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Async
    public void sendResetPasswordEmail(String toEmail, String token) {
        try {
            // Constrói o link que o usuário vai clicar no front-end ou mobile
            String resetUrl = "https://localhost:8080/reset-password?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(toEmail);
            message.setSubject("Recuperação de Senha - Soul");
            message.setText("Você solicitou a redefinição de sua senha.\n\n" +
                    "Utilize o token abaixo ou acesse o link (válido por 15 minutos):\n" +
                    "Token: " + token + "\n\n" +
                    "Link: " + resetUrl + "\n\n" +
                    "Se você não fez essa solicitação, ignore este e-mail.");

            mailSender.send(message);
        } catch (Exception e){
            System.err.println("ERRO (PRA TESTE): "+e.getMessage());
            e.printStackTrace();
        }
    }
}
