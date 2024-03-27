package com.itutorix.workshop.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itutorix.workshop.Services.EmailService;
import com.itutorix.workshop.config.JwtService;
import com.itutorix.workshop.token.Token;
import com.itutorix.workshop.token.TokenRepository;
import com.itutorix.workshop.token.TokenType;
import com.itutorix.workshop.user.User;
import com.itutorix.workshop.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String CONFIRMATION_URL = "http://localhost:8080/api/v1/auth/confirm?token=%s";
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    /**
     * Registers a new user.
     *
     * @param registerRequest the request containing the user's registration details
     * @return an AuthenticationResponse containing the generated JWT and refresh tokens
     * @throws IllegalStateException if the provided email is already taken
     */
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        User optionalUser = userRepository.findByEmail(registerRequest.email())
                    .orElse(null);

        if(optionalUser != null) {
            throw new IllegalStateException("Email [%s] is already taken".formatted(registerRequest.email()));
        }

        User user = User
                .builder()
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role(registerRequest.role())
                .build();
        User savedUser = userRepository.save(user);

        // Tokens generation
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);

        try {
            emailService.send(
                    savedUser.getEmail(),
                    savedUser.getFirstName(),
                    "confirm-email",
                    String.format(CONFIRMATION_URL, jwtToken)
            );
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    /**
     * Authenticates a user by validating their email and password.
     *
     * @param authenticationRequest the request containing the user's authentication details
     * @return an AuthenticationResponse containing the generated JWT and refresh tokens
     * @throws IllegalStateException if the provided email is not found in the database
     * @throws UsernameNotFoundException if the user with the provided email is not found
     */
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(),
                        authenticationRequest.password()
                )
        );

        User user = userRepository.findByEmail(authenticationRequest.email())
                .orElseThrow(() -> new IllegalStateException("User with email [%s] not found".formatted(authenticationRequest.email())));

        // Tokens generation
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);

        saveUserToken(user, jwtToken);

        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    /**
     * Revokes all tokens associated with the given user.
     *
     * @param user the user whose tokens should be revoked
     */
    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUserId(user.getId());

        if(validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Saves a new token for the given user.
     *
     * @param user the user whose token should be saved
     * @param jwtToken the JWT token to be saved
     */
    private void saveUserToken(User user, String jwtToken) {
        Token token = Token
                .builder()
                .expired(false)
                .revoked(false)
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .build();

        tokenRepository.save(token);
    }

    /**
     * Refreshes the user's token.
     *
     * @param request  the HTTP request containing the authorization header
     * @param response the HTTP response to send the new access token
     * @throws IOException if an error occurs while writing the response
     */
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUserName(refreshToken);

        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);

                AuthenticationResponse authenticationResponse = new AuthenticationResponse(
                        accessToken,
                        refreshToken
                );

                new ObjectMapper().writeValue(response.getOutputStream(), authenticationResponse);
            }
        }
    }

    /**
     * Confirms the user's email address.
     *
     * @param token the token used to confirm the user's email address
     * @return a confirmation message indicating whether the account has been successfully activated
     * @throws IllegalStateException if the token is not found
     * @throws UsernameNotFoundException if the user with the token is not found
     */
    public String confirm(String token) {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if (savedToken.isExpired()) {
            String jwtToken = jwtService.generateToken(savedToken.getUser());
            String refreshToken = jwtService.generateRefreshToken(savedToken.getUser());

            revokeAllUserTokens(savedToken.getUser());
            saveUserToken(savedToken.getUser(), jwtToken);
            try {
                emailService.send(
                        savedToken.getUser().getEmail(),
                        savedToken.getUser().getFirstName(),
                        "confirm-email",
                        String.format(CONFIRMATION_URL, jwtToken)
                );
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

            return "Token expired, a new token has been send to your email";
        }

        User user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
        return "<h1>Your account has been successfully activated</h1>";
    }
}
