package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.service.RequestPrivateService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestPrivateController {
    private final RequestPrivateService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable @Min(1) Integer userId, @RequestParam @Min(1) Integer eventId) {
        RequestDto createdRequest = service.createRequest(userId, eventId);
        log.info("New participation request has been created: {}", createdRequest);
        return createdRequest;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getUserRequests(@PathVariable @Min(1) Integer userId) {
        List<RequestDto> requests = service.getUserRequests(userId);
        log.info("Received list of participation requests made by user with id {}, {}", userId, requests);
        return requests;
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancelRequest(@PathVariable @Min(1) Integer userId, @PathVariable @Min(1) Integer requestId) {
        RequestDto requestDto = service.cancelRequest(userId, requestId);
        log.info("Participation request with id {} has been cancelled", requestId);
        return requestDto;
    }
}
