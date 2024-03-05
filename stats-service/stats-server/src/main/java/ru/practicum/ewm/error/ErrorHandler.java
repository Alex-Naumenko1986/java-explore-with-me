package ru.practicum.ewm.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.controller.StatsController;
import ru.practicum.ewm.exception.InvalidDateRangeException;

@RestControllerAdvice(assignableTypes = {StatsController.class})
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        log.error("Error occurred. Validation failed:{}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingParameterException(MissingServletRequestParameterException e) {
        log.error("Error occurred. Missing required parameter in request:{}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDateRangeException(InvalidDateRangeException e) {
        log.error("Error occurred. Invalid date range in request:{}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(Throwable t) {
        log.error("Error occurred", t);
        return new ErrorResponse(t.getMessage());
    }
}
