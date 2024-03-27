package com.itutorix.workshop.customer;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CustomerDTOMapper implements Function<Customer, CustomerDTO> {
    /**
     * Maps a {@link Customer} object to a {@link CustomerDTO} object.
     *
     * @param customer The {@link Customer} object to be mapped.
     * @return A new {@link CustomerDTO} object containing the mapped data.
     * @throws NullPointerException if the input {@code customer} is null.
     */
    @Override
    public CustomerDTO apply(Customer customer) {
        if (customer == null) {
            throw new NullPointerException("The customer is null");
        }

        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getCreateDate(),
                customer.getLastModified(),
                customer.getCreatedBy(),
                customer.getLastModifiedBy()
        );
    }
}
