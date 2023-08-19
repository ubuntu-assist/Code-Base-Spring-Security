package com.itutorix.workshop.auth;

import com.itutorix.workshop.config.JwtService;
import com.itutorix.workshop.token.Token;
import com.itutorix.workshop.token.TokenRespository;
import com.itutorix.workshop.token.TokenType;
import com.itutorix.workshop.user.Role;
import com.itutorix.workshop.user.User;
import com.itutorix.workshop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRespository tokenRespository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(registerRequest.email());

        if(optionalUser.isPresent()) {
            throw new IllegalStateException("Email [%s] is already taken".formatted(registerRequest.email()));
        }

        User user = User
                .builder()
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role(Role.USER)
                .build();
        User savedUser = userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        saveUserToken(savedUser, jwtToken);

        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(),
                        authenticationRequest.password()
                )
        );

        User user = userRepository.findByEmail(authenticationRequest.email())
                .orElseThrow(() -> new IllegalStateException("User with email [%s] not found".formatted(authenticationRequest.email())));

        String jwtToken = jwtService.generateToken(user);

        revokeAllUserTokens(user);

        saveUserToken(user, jwtToken);

        return new AuthenticationResponse(jwtToken);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRespository.findAllValidTokensByUserId(user.getId());

        if(validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRespository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token
                .builder()
                .expired(false)
                .revoked(false)
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .build();

        tokenRespository.save(token);
    }
}
