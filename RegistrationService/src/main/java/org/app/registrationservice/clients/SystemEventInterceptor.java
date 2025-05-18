package org.app.registrationservice.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.systemevent.proto.LogEventRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class SystemEventInterceptor implements HandlerInterceptor {
    private static final Pattern RESOURCE_ID_PATTERN = Pattern.compile("/registrations/([^/]+)(?:/|$)");

    private final SystemEventClient systemEventClient;
    private final ObjectMapper objectMapper;
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        startTime.set(System.currentTimeMillis());
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            long duration = System.currentTimeMillis() - startTime.get();

            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) response;

            String requestBody = new String(requestWrapper.getContentAsByteArray());
            String responseBody = new String(responseWrapper.getContentAsByteArray());
            responseWrapper.copyBodyToResponse();

            String endpoint = request.getRequestURI();
            String resourceId = extractResourceId(endpoint);

            String userId = request.getHeader("X-User-ID");

            LogEventRequest eventRequest = LogEventRequest.newBuilder()
                    .setServiceName("RegistrationsService")
                    .setEndpoint(endpoint)
                    .setHttpMethod(request.getMethod())
                    .setResourceId(resourceId)
                    .setRequestBody(requestBody)
                    .setResponseBody(responseBody)
                    .setStatusCode(response.getStatus())
                    .setUserId(userId != null ? userId : "anonymous")
                    .setTimestamp(System.currentTimeMillis())
                    .setDurationMs(duration)
                    .build();

            systemEventClient.logEvent(eventRequest);

        } catch (Exception e) {
            log.error("Error logging system event", e);
        } finally {
            startTime.remove();
        }
    }

    private String extractResourceId(String endpoint) {
        Matcher matcher = RESOURCE_ID_PATTERN.matcher(endpoint);
        if (matcher.find()) {
            String potentialId = matcher.group(1);
            try {
                UUID.fromString(potentialId);
                return potentialId;
            } catch (IllegalArgumentException e) {
                return potentialId;
            }
        }
        return "unknown";
    }
}
