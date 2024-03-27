package com.itutorix.workshop.customer;

import java.time.LocalDateTime;

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Integer age,
        LocalDateTime createDate,
        LocalDateTime lastModified,
        Integer createdBy,
        Integer lastModifiedBy
) {}



