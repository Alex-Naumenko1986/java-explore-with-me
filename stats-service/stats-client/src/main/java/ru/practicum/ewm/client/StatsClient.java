package ru.practicum.ewm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.constant.Constants;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.error.handler.RestTemplateResponseErrorHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StatsClient {
    private final RestTemplate restTemplate;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);
    private final String statsServerUrl = "http://localhost:9090";

    public StatsClient(RestTemplateBuilder builder) {
        restTemplate =
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(statsServerUrl))
                        .errorHandler(new RestTemplateResponseErrorHandler())
                        .build();
    }

    public void createEndpointHit(EndpointHitDto endpointHitDto) {
        log.info("Sending request to create new endpoint hit: {}", endpointHitDto);
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, defaultHeaders());

        restTemplate.postForObject("/hit", requestEntity, Object.class);
    }

    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        String startEncoded = UriUtils.encode(timeFormatter.format(start), StandardCharsets.UTF_8);
        String endEncoded = UriUtils.encode(timeFormatter.format(end), StandardCharsets.UTF_8);

        Map<String, Object> parameters = Map.of(
                "start", startEncoded,
                "end", endEncoded,
                "uris", uris,
                "unique", unique
        );
        log.info("Sending request to get endpoint hit statistics, start {}, end {}, uris {}, unique {}",
                start, end, uris, unique);

        String requestUrl = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        ViewStatsDto[] dtoArray = restTemplate.getForObject(requestUrl, ViewStatsDto[].class, parameters);
        if (dtoArray != null) {
            return List.of(dtoArray);
        } else {
            return null;
        }
    }


    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
