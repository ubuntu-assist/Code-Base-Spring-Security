package com.itutorix.workshop.customer;

public record NewCustomerRequest(
        String name,
        String email,
        Integer age,
        String password
) {}
