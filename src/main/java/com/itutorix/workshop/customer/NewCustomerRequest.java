package com.itutorix.workshop.customer;

import jakarta.validation.constraints.*;

public record NewCustomerRequest(
        @NotEmpty(message = "The name should not be empty")
        String name,
        @NotEmpty(message = "The email should not be empty")
        @Email(message = "Email should be valid")
        String email,
        @NotNull(message = "The age should not be null")
        @Positive(message = "Age should be positive")
        @Max(value = 45, message = "Age should not be greater than 45")
        Integer age,
        @NotEmpty(message = "The password should not be empty")
        String password
) {}
