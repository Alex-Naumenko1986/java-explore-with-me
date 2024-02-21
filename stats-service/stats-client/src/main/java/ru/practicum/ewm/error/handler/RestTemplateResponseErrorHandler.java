package ru.practicum.ewm.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import ru.practicum.ewm.error.exception.StatsClientResponseException;

import java.io.IOException;

@Slf4j
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is5xxServerError() ||
                response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.info("Error occured when receiving data from stats server, status code: {}, status text: {}",
                response.getStatusCode(), response.getStatusText());
        throw new StatsClientResponseException(String.format("Exception invoked when receiving data " +
                        "from stats server. Status code: %s. Status text: %s", response.getStatusCode(),
                response.getStatusText()));
    }
}
