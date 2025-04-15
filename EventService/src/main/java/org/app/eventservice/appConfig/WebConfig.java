package org.app.eventservice.appConfig;

import lombok.RequiredArgsConstructor;
import org.app.eventservice.clients.SystemEventInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SystemEventInterceptor systemEventInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(systemEventInterceptor);
    }
}
