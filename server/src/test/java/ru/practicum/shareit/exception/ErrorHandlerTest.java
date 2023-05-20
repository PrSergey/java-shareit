package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    ErrorHandler errorHandler;

    @BeforeEach
    public void createErrorHandler() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleIncorrectParameterException() {
        ValidationException ex = new ValidationException("некорректная валидация");
        ErrorResponse errorResponse = errorHandler.handleIncorrectParameterException(ex);
        assertEquals(errorResponse.getError(), ex.getMessage());
    }

    @Test
    void handleUserNotFoundException() {
        ExistenceException ex = new ExistenceException("некорректные данные");
        ErrorResponse errorResponse = errorHandler.handleUserNotFoundException(ex);
        assertEquals(errorResponse.getError(), ex.getMessage());
    }
}