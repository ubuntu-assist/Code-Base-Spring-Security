package com.itutorix.workshop.customer;
public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Integer age
) {}