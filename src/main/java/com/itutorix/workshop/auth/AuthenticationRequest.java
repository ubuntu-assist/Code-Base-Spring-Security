package com.itutorix.workshop.auth;

public record AuthenticationRequest(
    String email,
    String password
) {
}
