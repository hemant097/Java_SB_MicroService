package com.example.eCommerce.api_gateway.filters;

import com.example.eCommerce.api_gateway.service.JwtService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

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
        return ((exchange, chain) -> {

            if(!config.isEnabled()){
                return chain.filter(exchange);
            }
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            String token = "";

            if (authHeader == null){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
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

        });
    }


    @Data
    public static class Config{
        private boolean isEnabled;
    }

}
