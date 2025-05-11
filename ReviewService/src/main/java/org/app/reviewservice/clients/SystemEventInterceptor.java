package org.app.reviewservice.clients;

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
    private static final Pattern RESOURCE_ID_PATTERN = Pattern.compile("/api/v1/reviews/(\\d+)(?:/|$)");

    private final SystemEventClient systemEventClient;
    private final ObjectMapper objectMapper;
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Wrap the request with ContentCachingRequestWrapper if it's not already wrapped
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        // Start timing the request
        startTime.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            long duration = System.currentTimeMillis() - startTime.get();

            // Cast request and response to their respective wrappers
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) response;

            // Get request and response bodies
            String requestBody = new String(requestWrapper.getContentAsByteArray());
            String responseBody = new String(responseWrapper.getContentAsByteArray());
            responseWrapper.copyBodyToResponse();

            // Get endpoint and extract resource ID
            String endpoint = request.getRequestURI();
            String resourceId = extractResourceId(endpoint);

            // Get user ID from header
            String userId = request.getHeader("X-User-ID");

            // Create the LogEventRequest and log the event
            LogEventRequest eventRequest = LogEventRequest.newBuilder()
                    .setServiceName("EventService")
                    .setEndpoint(endpoint)
                    .setHttpMethod(request.getMethod())
                    .setResourceId(resourceId != null ? resourceId : "unknown")
                    .setRequestBody(requestBody)
                    .setResponseBody(responseBody)
                    .setStatusCode(response.getStatus())
                    .setUserId(userId != null ? userId : "anonymous")
                    .setTimestamp(System.currentTimeMillis())
                    .setDurationMs(duration)
                    .build();

            // Log the event using the systemEventClient
            systemEventClient.logEvent(eventRequest);

        } catch (Exception e) {
            log.error("Error logging system event", e);
        } finally {
            startTime.remove();
        }
    }

    // Method to extract the resource ID from the endpoint URI
    private String extractResourceId(String endpoint) {
        Matcher matcher = RESOURCE_ID_PATTERN.matcher(endpoint);
        if (matcher.find()) {
            String potentialId = matcher.group(1);
            try {
                UUID.fromString(potentialId);  // Check if it's a valid UUID
                return potentialId;
            } catch (IllegalArgumentException e) {
                return potentialId;  // Return it as a string if it's not a UUID
            }
        }
        return null;
    }
}