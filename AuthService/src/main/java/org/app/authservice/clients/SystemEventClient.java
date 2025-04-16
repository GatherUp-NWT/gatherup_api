package org.app.authservice.clients;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.app.systemevent.proto.LogEventRequest;
import org.app.systemevent.proto.SystemEventServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SystemEventClient {
    private final ManagedChannel channel;
    private final SystemEventServiceGrpc.SystemEventServiceBlockingStub blockingStub;

    public SystemEventClient(
            @Value("${system-events.host:localhost}") String host,
            @Value("${system-events.port:9090}") int port) {

        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        this.blockingStub = SystemEventServiceGrpc.newBlockingStub(channel);
    }

    public void logEvent(LogEventRequest request) {
        try {
            blockingStub.logEvent(request);
        } catch (Exception e) {
            System.err.println("Failed to log event: " + e.getMessage());
        }
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
