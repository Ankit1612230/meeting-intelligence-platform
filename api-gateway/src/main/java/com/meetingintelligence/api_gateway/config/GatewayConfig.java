package com.meetingintelligence.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;
@Configuration
public class GatewayConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Bean
    RouterFunction<ServerResponse> authRoute() {
        return route("auth-service")
                .route(path("/auth-service/**"), http())
                .before(stripPrefix(1))
                .filter(lb("AUTH-SERVICE"))
                .build();
    }
    @Bean
    RouterFunction<ServerResponse> meetingRoute() {
        return route("meeting-service")
                .route(path("/meeting-service/**"), http())
                .before(stripPrefix(1))
                .filter(lb("MEETING-SERVICE"))
                .build();
    }
}