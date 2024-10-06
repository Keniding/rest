package com.provias.backend.service_gateway.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthServiceFilter extends AbstractGatewayFilterFactory<AuthServiceFilter.Config> {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public AuthServiceFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                return webClientBuilder.build()
                        .post()
                        .uri("http://localhost:8083/api/auth/validate")
                        .bodyValue(token)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new RuntimeException("Token inválido")))
                        .bodyToMono(Void.class)
                        .flatMap(response -> chain.filter(exchange));
            }

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }

    public static class Config {
        // Configuración del filtro si es necesaria
    }
}
