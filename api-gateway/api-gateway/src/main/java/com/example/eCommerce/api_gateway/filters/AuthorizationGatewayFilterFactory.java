package com.example.eCommerce.api_gateway.filters;

import com.example.eCommerce.api_gateway.service.JwtService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;


@Component
@Slf4j
public class AuthorizationGatewayFilterFactory extends
            AbstractGatewayFilterFactory<AuthorizationGatewayFilterFactory.Config> {

    private final JwtService jwtService;

    public AuthorizationGatewayFilterFactory(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (authHeader == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            //assuming getting a Bearer token
            String token = authHeader.substring(7);
            Long userId = jwtService.getUserIdFromToken(token);
            String role = jwtService.getRoleFromToken(token);

            if (!config.allowed.contains(role)) {
                return chain.filter(exchange);
            }

            //mutating the request, as mutate creates a builder, and thus a new request
            ServerHttpRequest mutatedReq = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", userId.toString())
                    .build();

            //mutated exchange with the new request
            ServerWebExchange newExchange = exchange
                    .mutate()
                    .request(mutatedReq)
                    .build();

            //passing the new exchange down the filter chain
            return chain.filter(newExchange);


        });
    }


    @Data
    public static class Config {
        private List<String> allowed;
    }
}

