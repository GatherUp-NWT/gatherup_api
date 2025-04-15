package org.app.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.entities.SystemEvent;
import org.app.repositories.SystemEventRepository;
import org.app.systemevent.proto.LogEventRequest;
import org.app.systemevent.proto.LogEventResponse;
import org.app.systemevent.proto.SystemEventServiceGrpc;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class SystemEventService extends SystemEventServiceGrpc.SystemEventServiceImplBase {

    private final SystemEventRepository systemEventRepository;

    @Override
    public void logEvent(LogEventRequest request, StreamObserver<LogEventResponse> responseObserver) {
        try {
            SystemEvent event = new SystemEvent();
            event.setServiceName(request.getServiceName());
            event.setEndpoint(request.getEndpoint());
            event.setHttpMethod(request.getHttpMethod());
            event.setResourceId(request.getResourceId());
            event.setRequestBody(request.getRequestBody());
            event.setResponseBody(request.getResponseBody());
            event.setStatusCode(request.getStatusCode());
            event.setUserId(request.getUserId());
            event.setTimestamp(Instant.ofEpochMilli(request.getTimestamp()));
            event.setDurationMs(request.getDurationMs());

            systemEventRepository.save(event);

            log.info("Logged system event: {} {} - status: {}, duration: {}ms",
                    event.getHttpMethod(), event.getEndpoint(),
                    event.getStatusCode(), event.getDurationMs());

            LogEventResponse response = LogEventResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Event logged successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error logging system event", e);

            LogEventResponse response = LogEventResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to log event: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
