package org.app.eventservice.appConfig;

import jakarta.persistence.EntityManagerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public StatisticsService statisticsService(EntityManagerFactory emf) {
        return new StatisticsService(emf);
    }
}
