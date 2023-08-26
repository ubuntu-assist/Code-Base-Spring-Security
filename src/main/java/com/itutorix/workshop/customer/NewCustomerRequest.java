package com.itutorix.workshop.customer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record NewCustomerRequest(
        @NotNull(message = "The name should not be null")
        @NotEmpty(message = "The name should not be empty")
        String name,
        @NotNull(message = "The email should not be null")
        @NotEmpty(message = "The email should not be empty")
        String email,
        @NotNull(message = "The age should not be null")
        Integer age,
        @NotNull(message = "The password should not be null")
        @NotEmpty(message = "The password should not be empty")
        String password
) {}
