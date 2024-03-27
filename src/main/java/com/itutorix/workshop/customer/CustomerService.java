package com.itutorix.workshop.customer;

import com.itutorix.workshop.validators.ObjectsValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

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

    /**
     * Returns a list of all customers in the system.
     *
     * @return a list of all customers
     */
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository
               .findAll()
               .stream()
               .map(customerDTOMapper)
               .toList();
    }

    /**
     * Retrieves a single customer from the system by its id.
     *
     * @param id the unique identifier of the customer
     * @return the requested customer as a DTO, or throws an {@link IllegalArgumentException} if the customer with the given id is not found
     */
    public CustomerDTO getSingleCustomer(Integer id) {
        return customerRepository.findById(id)
                .map(customerDTOMapper)
                .orElseThrow(() -> new IllegalArgumentException("Customer with id [%s] not found".formatted(id)));
    }

    /**
     * Creates a new customer in the system.
     *
     * @param request the request containing the details of the new customer
     * @return a response containing a message and the id of the newly created customer
     * @throws IllegalArgumentException if the email provided in the request is already taken
     */
    public NewCustomerResponse createCustomer(NewCustomerRequest request) {
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
                .password(request.password())
                .build();

        Customer savedCustomer = customerRepository.save(newCustomer);
        return new NewCustomerResponse(
                "Customer successfully created",
                savedCustomer.getId()
        );
    }

    /**
     * Deletes a customer from the system by its id.
     *
     * @param id the unique identifier of the customer to be deleted
     * @throws IllegalArgumentException if the customer with the given id is not found
     */
    public void deleteCustomer(Integer id) {
        boolean exists = customerRepository.existsById(id);

        if(!exists)
            throw new IllegalArgumentException("Customer with id [%s] not found".formatted(id));

        customerRepository.deleteById(id);
    }

    /**
     * Updates an existing customer in the system.
     *
     * @param id the unique identifier of the customer to be updated
     * @param request the request containing the details of the new customer
     * @throws IllegalArgumentException if the customer with the given id is not found
     */
    public void updateCustomer(Integer id, NewCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with id [%s] not found".formatted(id)));

        boolean changes = false;

        changes |= updateFieldIfNecessary(request.email(), customer.getEmail(), customer::setEmail);
        changes |= updateFieldIfNecessary(request.name(), customer.getName(), customer::setName);
        changes |= updateFieldIfNecessary(request.age(), customer.getAge(), customer::setAge);

        if (!changes) {
            throw new IllegalArgumentException("No changes found");
        }

        customerRepository.save(customer);
    }

    /**
     * Compares the new value with the current value and updates the current value if they are different.
     * Returns true if an update was made, false otherwise.
     *
     * @param newValue the new value to be set
     * @param currentValue the current value
     * @param updateFunction the function to update the current value
     * @param <T> the type of the value
     * @return true if an update was made, false otherwise
     */
    private <T> boolean updateFieldIfNecessary(T newValue, T currentValue, Consumer<T> updateFunction) {
        if (newValue != null && !newValue.equals(currentValue)) {
            updateFunction.accept(newValue);
            return true;
        }
        return false;
    }
}