package ru.practicum.ewm.request.exception;

public class IllegalRequestOperationException extends RuntimeException {
    public IllegalRequestOperationException(String message) {
        super(message);
    }
}
