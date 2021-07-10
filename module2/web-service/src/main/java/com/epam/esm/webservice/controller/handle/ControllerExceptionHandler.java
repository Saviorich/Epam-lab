package com.epam.esm.webservice.controller.handle;

import com.epam.esm.webservice.service.exception.CertificateNotFoundException;
import com.epam.esm.webservice.service.exception.TagNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({CertificateNotFoundException.class, TagNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> resourceNotFoundException(RuntimeException e) {
        int customErrorCode = HttpStatus.NOT_FOUND.value() * 10 + 1;
        return new ResponseEntity<>(new ErrorMessage(e.getLocalizedMessage(), customErrorCode), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMissingPathVariableException(MethodArgumentTypeMismatchException e) {
        int customErrorCode = HttpStatus.BAD_REQUEST.value() * 10 + 1;
        return new ResponseEntity<>(new ErrorMessage(e.getLocalizedMessage(), customErrorCode), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleInternalError(Throwable t) {
        int customErrorCode = HttpStatus.INTERNAL_SERVER_ERROR.value() * 10 + 1;
        return new ResponseEntity<>(new ErrorMessage(t.getLocalizedMessage(), customErrorCode), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
