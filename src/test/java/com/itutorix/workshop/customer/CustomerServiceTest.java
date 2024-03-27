package com.itutorix.workshop.customer;

import com.itutorix.workshop.exceptions.ObjectNotValidException;
import com.itutorix.workshop.validators.ObjectsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerDTOMapper customerDTOMapper;
    @Mock
    private ObjectsValidator<NewCustomerRequest> validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void itShouldCreateCustomer() {
        // Given
        NewCustomerRequest request = new NewCustomerRequest(
                "FOPA KUETE",
                "duclair.fopa@kimbocare.com",
                21,
                "demoFkd$2"
        );

        Customer newCustomer = Customer
                .builder()
                .age(request.age())
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .build();

        Customer savedCustomer = Customer
                .builder()
                .id(1)
                .age(request.age())
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .build();

        when(customerRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(customerRepository.save(newCustomer))
                .thenReturn(savedCustomer);

        // When
        NewCustomerResponse response = customerService.createCustomer(request);

        // Then
        assertEquals("Customer successfully created", response.message());
        assertNotNull(response.id());
        verify(validator).validate(request);
        verify(customerRepository, times(1)).findByEmail(request.email());
        verify(customerRepository, times(1)).save(newCustomer);
    }

    @Test
    void itShouldThrowWhenEmailAlreadyExists() {
        // Given
        NewCustomerRequest request = new NewCustomerRequest("John Doe", "john@example.com", 30, "password123");
        Customer existingCustomer = Customer
                .builder()
                .id(1)
                .age(request.age())
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .build();

        when(customerRepository.findByEmail(request.email())).thenReturn(Optional.of(existingCustomer));

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(request));
        assertTrue(thrown.getMessage().contains("Email [%s] is already taken".formatted(request.email())));
        verify(validator).validate(request);
        verify(customerRepository, times(1)).findByEmail(request.email());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void itShouldThrowWhenValidationFails() {
        // Given
        NewCustomerRequest request = new NewCustomerRequest("Fopa Kuete", "invalid_email", 30, "password123");
        Set<String> errorMessages = new HashSet<>();
        errorMessages.add("Email is invalid");

        doThrow(new ObjectNotValidException(errorMessages)).when(validator).validate(request);

        // When & Then
        ObjectNotValidException thrown = assertThrows(ObjectNotValidException.class, () -> customerService.createCustomer(request));
        verify(validator).validate(request);
        verify(customerRepository, never()).findByEmail(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
        assertTrue(thrown.getErrorMessages().contains("Email is invalid"));
    }

    @Test
    void itShouldGetAllCustomers() {
        // Given
        Customer customer = new Customer(
                1,
                22,
                "Fopa Kuete",
                "duclair.fopa@kimbocare.com",
                "demoFkd$2",
                LocalDateTime.now(),
                null,
                1,
                1
        );

        List<Customer> customers = List.of(customer);

        CustomerDTO customerDTO = new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getCreateDate(),
                customer.getLastModified(),
                customer.getCreatedBy(),
                customer.getLastModifiedBy()
        );

        when(customerRepository.findAll()).thenReturn(customers);
        when(customerDTOMapper.apply(customer)).thenReturn(customerDTO);

        // When
        List<CustomerDTO> customerDTOList = customerService.getAllCustomers();

        // Then

        assertEquals(customerDTOList.size(), customers.size());
        verify(customerRepository, times(1)).findAll();
        verify(customerDTOMapper, times(1)).apply(customer);
    }

    @Test
    void itShouldGetSingleCustomer() {
        // Given
        Integer customerId = 1;
        Customer customer = new Customer(
                customerId,
                22,
                "Fopa Kuete",
                "duclair.fopa@kimbocare.com",
                "demoFkd$2",
                LocalDateTime.now(),
                null,
                1,
                1
        );

        CustomerDTO customerDTO = new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getCreateDate(),
                customer.getLastModified(),
                customer.getCreatedBy(),
                customer.getLastModifiedBy()
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerDTOMapper.apply(customer)).thenReturn(customerDTO);

        // When
        CustomerDTO dto = customerService.getSingleCustomer(customerId);

        // Then
        assertEquals(customerDTO, dto);
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerDTOMapper, times(1)).apply(customer);
    }

    @Test
    void itShouldThrowWhenSingleCustomerNotFound() {
        // Given
        Integer customerId = 1;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> customerService.getSingleCustomer(customerId));
        assertTrue(thrown.getMessage().contains("Customer with id [%s] not found".formatted(customerId)));
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerDTOMapper, never()).apply(any(Customer.class));
    }

    @Test
    void itShouldDeleteCustomer() {
        // Given
        Integer customerId = 1;
        when(customerRepository.existsById(customerId)).thenReturn(true);

        // When
        customerService.deleteCustomer(customerId);

        // Then
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void itShouldThrowWhenCustomerToDeleteNotFound() {
        // Given
        Integer customerId = 1;
        when(customerRepository.existsById(customerId)).thenReturn(false);

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> customerService.deleteCustomer(customerId));
        assertTrue(thrown.getMessage().contains("Customer with id [%s] not found".formatted(customerId)));

        verify(customerRepository, never()).deleteById(anyInt());
    }

    @Test
    void itShouldUpdateCustomer() {
        // Given
        Integer customerId = 1;
        NewCustomerRequest request = new NewCustomerRequest(
                "FOPA KUETE",
                "duclair.fopa@kimbocare.com",
                21,
                "demoFkd$2"
        );

        Customer customer = Customer
                .builder()
                .id(1)
                .age(request.age())
                .name("Duclair")
                .email("fopaduclair2000@gmail.com")
                .password(request.password())
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When
        customerService.updateCustomer(customerId, request);

        // Then
        assertEquals(request.email(), customer.getEmail());
        assertEquals(request.name(), customer.getName());
        assertEquals(request.age(), customer.getAge());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void itShouldThrowWhenCustomerToUpdateNotFound() {
        // Given
        Integer customerId = 1;
        NewCustomerRequest request = new NewCustomerRequest(
                "FOPA KUETE",
                "duclair.fopa@kimbocare.com",
                21,
                "demoFkd$2"
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> customerService.updateCustomer(customerId, request));
        assertTrue(thrown.getMessage().contains("Customer with id [%s] not found".formatted(customerId)));
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void itShouldThrowWhenNoChangesFound() {
        // Given
        Integer customerId = 1;
        NewCustomerRequest request = new NewCustomerRequest(
                "FOPA KUETE",
                "duclair.fopa@kimbocare.com",
                21,
                "demoFkd$2"
        );

        Customer customer = Customer
                .builder()
                .id(1)
                .age(request.age())
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> customerService.updateCustomer(customerId, request));
        assertTrue(thrown.getMessage().contains("No changes found"));
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any(Customer.class));
    }
}