package ru.practicum.ewm.user.exception;

public class IllegalSubscriptionOperationException extends RuntimeException {
    public IllegalSubscriptionOperationException(String message) {
        super(message);
    }
}
