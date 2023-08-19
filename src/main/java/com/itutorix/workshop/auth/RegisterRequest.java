package com.itutorix.workshop.auth;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
