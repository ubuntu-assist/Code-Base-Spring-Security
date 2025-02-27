package com.mock.io.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class ObjectNotValidException extends RuntimeException {

    private final Set<String> errorMessages;
}
