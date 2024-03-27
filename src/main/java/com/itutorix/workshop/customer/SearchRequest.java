package com.itutorix.workshop.customer;

public record SearchRequest(
        String name,
        String email,
        Integer age
) {
}
