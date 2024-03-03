package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.service.StatsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createEndpointHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Received request to save endpoint hit: {}", endpointHitDto);
        service.createEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStatistics(@RequestParam String start,
                                            @RequestParam String end,
                                            @RequestParam(defaultValue = "") String[] uris,
                                            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Getting statistics with request parameters: start {}, end {}, uris {}, unique {}",
                start, end, uris, unique);
        return service.getStatistics(start, end, uris, unique);
    }


}
