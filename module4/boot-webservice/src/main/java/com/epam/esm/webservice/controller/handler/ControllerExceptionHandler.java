package com.epam.esm.webservice.controller.handler;

import com.epam.esm.webservice.service.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final int SHIFT = 10;

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> resourceNotFoundException(ResourceNotFoundException e) {
        int customErrorCode = HttpStatus.NOT_FOUND.value() * SHIFT + e.getResourceNumber();
        return new ResponseEntity<>(new ErrorMessage(e.getLocalizedMessage(), customErrorCode), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidPageNumberException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> invalidPageNumberException(InvalidPageNumberException e) {
        int customErrorCode = HttpStatus.BAD_REQUEST.value() * SHIFT;
        return new ResponseEntity<>(new ErrorMessage(e.getLocalizedMessage(), customErrorCode), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnauthorizedAccessException.class})
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> illegalAccessException(UnauthorizedAccessException e) {
        int customErrorCode = HttpStatus.FORBIDDEN.value() * SHIFT;
        return new ResponseEntity<>(new ErrorMessage(e.getLocalizedMessage(), customErrorCode), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ResponseEntity<Object> dataIntegrityViolationException(DataIntegrityViolationException e) {
        int customErrorCode = HttpStatus.CONFLICT.value() * SHIFT;
        String message = "Unable to delete resource";
        return new ResponseEntity<>(new ErrorMessage(message, customErrorCode), HttpStatus.CONFLICT);
    }
}
