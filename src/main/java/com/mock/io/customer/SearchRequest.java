package com.mock.io.customer;

public record SearchRequest(
        String name,
        String email,
        Integer age
) {
}
