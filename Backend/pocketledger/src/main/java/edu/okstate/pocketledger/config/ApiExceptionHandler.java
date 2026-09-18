package edu.okstate.pocketledger.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice 
public class ApiExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationError(MethodArgumentNotValidException exception) {
        FieldError firstError = exception.getBindingResult().getFieldError();
        String message = firstError == null
            ? "The request is not valid."
            : firstError.getField() + " " + firstError.getDefaultMessage();
        return error("VALIDATION_ERROR", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleUnreadableBody(HttpMessageNotReadableException exception) {
        return error("INVALID_REQUEST", "The request could not be read. Check the field names and values.");
    }

    private Map<String, Object> error(String code, String message) {
        return Map.of("error", Map.of("code", code, "message", message));
    }
}
