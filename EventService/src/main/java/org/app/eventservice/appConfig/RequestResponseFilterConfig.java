package org.app.eventservice.appConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Configuration
public class RequestResponseFilterConfig {
    @Bean
    public FilterRegistrationBean<ContentCachingFilter> contentCachingFilter() {
        FilterRegistrationBean<ContentCachingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ContentCachingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    public static class ContentCachingFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            // Only wrap if not already wrapped
            HttpServletRequest requestToUse = request;
            if (!(request instanceof ContentCachingRequestWrapper)) {
                requestToUse = new ContentCachingRequestWrapper(request);
            }

            HttpServletResponse responseToUse = response;
            if (!(response instanceof ContentCachingResponseWrapper)) {
                responseToUse = new ContentCachingResponseWrapper(response);
            }

            // Store the wrapper in the request for later retrieval
            requestToUse.setAttribute("CACHED_RESPONSE_WRAPPER", responseToUse);

            try {
                filterChain.doFilter(requestToUse, responseToUse);
            } finally {
                // Make sure to copy the content to the response if needed
                if (responseToUse instanceof ContentCachingResponseWrapper) {
                    ((ContentCachingResponseWrapper) responseToUse).copyBodyToResponse();
                }
            }
        }
    }
}
