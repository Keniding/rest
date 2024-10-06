package com.provias.backend.service_auth.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return getVoidMono(exchange, chain, logger);
    }

    public static Mono<Void> getVoidMono(ServerWebExchange exchange, WebFilterChain chain, Logger logger) {
        ServerHttpRequest request = exchange.getRequest();
        logger.info("Request: {} {}", request.getMethod(), request.getURI());
        request.getHeaders().forEach((name, values) -> {
            values.forEach(value -> logger.info("{}: {}", name, value));
        });
        return chain.filter(exchange);
    }
}
