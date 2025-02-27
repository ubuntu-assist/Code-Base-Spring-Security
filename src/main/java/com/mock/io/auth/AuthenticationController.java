package com.mock.io.auth;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@Hidden
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) throws MessagingException {
        authenticationService.register(registerRequest);
        return ResponseEntity.accepted().build();
    }

//    @PostMapping("authenticate")
//    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
//        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
//    }
//
//    @PostMapping("refresh-token")
//    public void refreshToken(
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) throws IOException {
//        authenticationService.refreshToken(request, response);
//    }
//
//    @GetMapping("confirm")
//    public ResponseEntity<String> confirm(@RequestParam String token) {
//        return ResponseEntity.ok(authenticationService.confirm(token));
//    }
}
