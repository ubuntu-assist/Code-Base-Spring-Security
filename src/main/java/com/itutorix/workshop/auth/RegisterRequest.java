package com.itutorix.workshop.auth;

import com.itutorix.workshop.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotEmpty(message = "The first name should not be empty")
        String firstName,
        @NotEmpty(message = "The last name should not be empty")
        String lastName,
        @NotEmpty(message = "The name should not be empty")
        @Email(message = "Email should be valid")
        String email,
        @NotEmpty(message = "The name should not be empty")
        String password,
        @NotNull(message = "Role should not be null")
        Role role
) {}
