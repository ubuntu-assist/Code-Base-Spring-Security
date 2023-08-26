package com.itutorix.workshop.customer;

import com.itutorix.workshop.validators.ObjectsValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerDTOMapper customerDTOMapper;
    private final ObjectsValidator<NewCustomerRequest> validator;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerDTOMapper customerDTOMapper,
            ObjectsValidator<NewCustomerRequest> validator
    ) {
        this.customerRepository = customerRepository;
        this.customerDTOMapper = customerDTOMapper;
        this.validator = validator;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository
                .findAll()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getSingleCustomer(Integer id) {
        return customerRepository.findById(id)
                .map(customerDTOMapper)
                .orElseThrow(() -> new IllegalArgumentException("Customer with id [%s] not found".formatted(id)));
    }

    public NewUserResponse createCustomer(NewCustomerRequest request) {
        validator.validate(request);

        Customer customer = customerRepository.findByEmail(request.email())
                .orElse(null);

        if(customer != null)
            throw new IllegalArgumentException("Email [%s] is already taken".formatted(request.email()));

        Customer newCustomer = Customer
                .builder()
                .age(request.age())
                .name(request.name())
                .email(request.email())
                .build();

        Customer savedCustomer = customerRepository.save(newCustomer);
        return new NewUserResponse(
                "Customer successfully created",
                savedCustomer.getId()
        );
    }

    public void deleteCustomer(Integer id) {
        boolean exists = customerRepository.existsById(id);

        if(!exists)
            throw new IllegalArgumentException("Customer with id [%s] not found".formatted(id));

        customerRepository.deleteById(id);
    }

    public void updateCustomer(Integer id, NewCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with id [%s] not found".formatted(id)));

        boolean changes = false;

        if(request.email() != null && !customer.getEmail().equals(request.email())) {
            customer.setEmail(request.email());
            changes = true;
        }

        if(request.name() != null && !customer.getName().equals(request.name())) {
            customer.setName(request.name());
            changes = true;
        }

        if(request.age() != null && !request.age().equals(customer.getAge())) {
            customer.setAge(request.age());
            changes = true;
        }

        if(!changes)
            throw new IllegalArgumentException("No changes found");

        customerRepository.save(customer);
    }

    public String throwException() {
        throw new IllegalStateException("There is an exception");
    }
}