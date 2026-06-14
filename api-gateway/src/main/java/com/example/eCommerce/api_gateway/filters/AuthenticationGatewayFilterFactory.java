package com.example.eCommerce.api_gateway.filters;

import com.example.eCommerce.api_gateway.service.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
public class AuthenticationGatewayFilterFactory extends
        AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private final JwtService jwtService;

    public AuthenticationGatewayFilterFactory(JwtService jwtService){
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){

                if(!config.isEnabled()){
                    return chain.filter(exchange);
                }

                String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
                String token = "";

                if (authHeader == null){
                    return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Authorization header is missing");
                }

                if (authHeader.startsWith("Bearer ")){
                    token = authHeader.substring(7);
                    Long userId = jwtService.getUserIdFromToken(token);

                    //mutating the request, as mutate creates a builder, and thus a new request
                    ServerHttpRequest mutatedReq = exchange.getRequest()
                            .mutate()
                            .header("X-User-Id",userId.toString())
                            .build();

                    //mutated exchange with the new request
                    ServerWebExchange newExchange = exchange
                            .mutate()
                            .request(mutatedReq)
                            .build();

                    //passing the new exchange down the filter chain
                    return chain.filter(newExchange);

                }
                return chain.filter(exchange);

            }
        };
    }


    @Data
    public static class Config{
        private boolean isEnabled;
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new LinkedHashMap<>();
        body.put("message", message);
        body.put("timestamp", Instant.now().toString());

        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(body);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete(); // safe fallback
        }
    }
}

