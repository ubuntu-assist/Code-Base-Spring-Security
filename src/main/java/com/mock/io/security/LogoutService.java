package com.mock.io.security;

import com.mock.io.user.Token;
import com.mock.io.user.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    /**
     * Handles the logout process by revoking the provided JWT token.
     *
     * @param request  The HTTP servlet request containing the Authorization header.
     * @param response The HTTP servlet response.
     * @param authentication The authentication object representing the user being logged out.
     *
     */
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        jwt = authHeader.substring(7);

        Token token = tokenRepository.findByToken(jwt).orElse(null);

        if (token != null) {
            token.setExpiresAt(LocalDateTime.now());
            tokenRepository.save(token);
        }
    }
}
