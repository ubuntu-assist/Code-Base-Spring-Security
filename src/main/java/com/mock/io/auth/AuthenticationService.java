package com.mock.io.auth;

import com.mock.io.email.EmailService;
import com.mock.io.email.EmailTemplateName;
import com.mock.io.role.Role;
import com.mock.io.role.RoleRepository;
import com.mock.io.security.JwtService;
import com.mock.io.user.Token;
import com.mock.io.user.TokenRepository;
import com.mock.io.user.User;
import com.mock.io.user.UserRepository;
import com.mock.io.validators.ObjectsValidator;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final ObjectsValidator<RegisterRequest> validator;
    private final RoleRepository roleRepository;

    /**
     * Registers a new user.
     *
     * @param registerRequest the request containing the user's registration details
     * @throws IllegalStateException if the provided email is already taken
     */
    public void register(RegisterRequest registerRequest) throws MessagingException {
        validator.validate(registerRequest);

        User optionalUser = userRepository.findByEmail(registerRequest.email())
                    .orElse(null);

        if(optionalUser != null) {
            throw new IllegalStateException("Email [%s] is already taken".formatted(registerRequest.email()));
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER wasn't initialized"));

        User user = User
                .builder()
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .roles(List.of(userRole))
                .enabled(false)
                .locked(false)
                .build();
        User savedUser = userRepository.save(user);
        sendValidationEmail(savedUser);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        String token = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                token,
                "Account Activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode();
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
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
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(),
                        authenticationRequest.password()
                )
        );

        var claims = new HashMap<String, Object>();
        User user = ((User)auth.getPrincipal());
        claims.put("fullname", user.fullName());
        String jwtToken = jwtService.generateToken(claims, user);

        return new AuthenticationResponse(jwtToken);
    }


    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token as expired. A new token has been sent to the same email address.");
        }

        User user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
