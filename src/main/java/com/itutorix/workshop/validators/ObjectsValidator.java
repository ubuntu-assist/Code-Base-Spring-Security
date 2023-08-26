package com.itutorix.workshop.validators;

import com.itutorix.workshop.customer.NewCustomerRequest;
import com.itutorix.workshop.exceptions.ObjectNotValidException;
import jakarta.validation.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ObjectsValidator<T> {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public void validate(T objectToValidate) {
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);

        if(!violations.isEmpty()) {
             Set<String> errorMessages = violations
                     .stream()
                     .map(ConstraintViolation::getMessage)
                     .collect(Collectors.toSet());

             throw new ObjectNotValidException(errorMessages);
        }
    }
}
