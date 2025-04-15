package org.app.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import lombok.extern.slf4j.Slf4j;
import org.app.service.SystemEventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class GrpcServerConfig {

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    @Bean
    public Server grpcServer(SystemEventService systemEventService) throws IOException {
        Server server = ServerBuilder.forPort(grpcPort)
                .addService(systemEventService)
                .build();

        server.start();
        log.info("gRPC Server started on port {}", grpcPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down gRPC server");
            server.shutdown();
        }));

        return server;
    }
}
