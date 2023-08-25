package com.itutorix.workshop.auth;

import com.itutorix.workshop.user.Role;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        Role role
) {
}
