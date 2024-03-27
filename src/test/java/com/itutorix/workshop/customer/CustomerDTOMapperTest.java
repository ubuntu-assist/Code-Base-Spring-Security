package com.itutorix.workshop.customer;

import com.itutorix.workshop.user.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDTOMapperTest {

    private CustomerDTOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerDTOMapper();
    }

    @Test
    void itShouldMapCustomerDTOToCustomer() {
        // Given
        Customer customer = Customer.builder()
                .id(1)
                .name("Fopa Duclair")
                .age(21)
                .email("duclair.fopa@kimbocare.com")
                .password("demoFkd$2000")
                .createDate(LocalDateTime.now())
                .createdBy(1)
                .lastModified(null)
                .lastModifiedBy(1)
                .build();

        // When
        CustomerDTO customerDTO = mapper.apply(customer);

        // Then
        assertEquals(customerDTO.id(), customer.getId());
        assertEquals(customerDTO.age(), customer.getAge());
        assertEquals(customerDTO.name(), customer.getName());
        assertEquals(customerDTO.createDate(), customer.getCreateDate());
        assertEquals(customerDTO.email(), customer.getEmail());
    }

    @Test
    void itShouldThrowWhenCustomerIsNull() {
        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> mapper.apply(null));
        assertEquals("The customer is null", nullPointerException.getMessage());
    }
}