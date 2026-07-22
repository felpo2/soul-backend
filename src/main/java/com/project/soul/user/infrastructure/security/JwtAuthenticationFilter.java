package com.project.soul.user.infrastructure.security;

import com.project.soul.user.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


    @Component
    public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtTokenService jwtTokenService;
        private final UserRepository userRepository;

        public JwtAuthenticationFilter(JwtTokenService jwtTokenService, UserRepository userRepository) {
            this.jwtTokenService = jwtTokenService;
            this.userRepository = userRepository;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            String token = recoverToken(request);

            if (token != null) {
                String email = jwtTokenService.validateTokenAndGetSubject(token);

                if (email != null) {
                    userRepository.findByEmail(email).ifPresent(user -> {
                        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    });
                }
            }

            filterChain.doFilter(request, response);
        }

        private String recoverToken(HttpServletRequest request) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null;
            }
            return authHeader.replace("Bearer ", "");
        }
    }

