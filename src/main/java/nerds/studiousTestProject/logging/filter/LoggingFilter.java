package nerds.studiousTestProject.logging.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.logging.util.ApiQueryCounter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {
    private static final String REQUEST_LOG_NO_BODY_FORMAT = "REQUEST :: METHOD: {}, URL: {}, AUTHORIZATION: {}";
    private static final String REQUEST_LOG_FORMAT = REQUEST_LOG_NO_BODY_FORMAT + ", BODY: {}";
    private static final String RESPONSE_LOG_NO_BODY_FORMAT = "RESPONSE :: STATUS_CODE: {}, METHOD: {}, URL: {}, QUERY_COUNT: {}, TIME_TAKEN: {}ms";
    private static final String RESPONSE_LOG_FORMAT = RESPONSE_LOG_NO_BODY_FORMAT + ", BODY: {}";
    private static final String QUERY_COUNT_WARNING_LOG_FORMAT = "쿼리가 {}번 이상 실행되었습니다.";

    private static final int QUERY_COUNT_WARNING_STANDARD = 10;
    private static final String START_OF_PARAMS = "?";
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String AUTHORIZATION = "Authorization";

    private final StopWatch apiTimer;
    private final ApiQueryCounter apiQueryCounter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

        apiTimer.start();
        filterChain.doFilter(cachingRequest, cachingResponse);
        apiTimer.stop();

        logRequestAndResponse(cachingRequest, cachingResponse);
        cachingResponse.copyBodyToResponse();
    }

    private void logRequestAndResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        logRequest(request);
        logResponse(request, response);
        warnByQueryCount();
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String requestBody = new String(request.getContentAsByteArray());
        String requestURIWithParams = getRequestURIWithParams(request);

        if (requestBody.isBlank()) {
            log.info(REQUEST_LOG_NO_BODY_FORMAT, request.getMethod(), requestURIWithParams, request.getHeader(AUTHORIZATION));
            return;
        }

        log.info(REQUEST_LOG_FORMAT, request.getMethod(), requestURIWithParams, request.getHeader(AUTHORIZATION), requestBody);
    }

    private void logResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        Optional<String> jsonResponseBody = getJsonResponseBody(response);
        int queryCount = apiQueryCounter.getCount();
        String requestURIWithParams = getRequestURIWithParams(request);

        if (jsonResponseBody.isEmpty()) {
            log.info(RESPONSE_LOG_NO_BODY_FORMAT, response.getStatus(), request.getMethod(),
                    requestURIWithParams, queryCount, apiTimer.getLastTaskTimeMillis());
            return;
        }

        log.info(RESPONSE_LOG_FORMAT, response.getStatus(), request.getMethod(), requestURIWithParams,
                queryCount, apiTimer.getLastTaskTimeMillis(), jsonResponseBody.get());
    }

    private String getRequestURIWithParams(ContentCachingRequestWrapper request) {
        String requestURI = request.getRequestURI();
        Map<String, String[]> params = request.getParameterMap();

        if (params.isEmpty()) {
            return requestURI;
        }

        String parsedParams = parseParams(params);
        return requestURI + parsedParams;
    }

    private String parseParams(Map<String , String[]> params) {
        String everyParamStrings = params.entrySet().stream()
                .map(this::toParamString)
                .collect(Collectors.joining(PARAM_DELIMITER));

        return START_OF_PARAMS + everyParamStrings;
    }

    private String toParamString(Map.Entry<String, String[]> entry) {
        String key = entry.getKey();
        StringBuilder builder = new StringBuilder();

        return Arrays.stream(entry.getValue())
                .map(value -> builder.append(key).append(KEY_VALUE_DELIMITER).append(value))
                .collect(Collectors.joining(PARAM_DELIMITER));
    }

    private Optional<String> getJsonResponseBody(ContentCachingResponseWrapper response) {
        if (Objects.equals(response.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            return Optional.of(new String(response.getContentAsByteArray()));
        }

        return Optional.empty();
    }

    private void warnByQueryCount() {
        int queryCount = apiQueryCounter.getCount();
        if (queryCount >= QUERY_COUNT_WARNING_STANDARD) {
            log.warn(QUERY_COUNT_WARNING_LOG_FORMAT, QUERY_COUNT_WARNING_STANDARD);
        }
    }
}
