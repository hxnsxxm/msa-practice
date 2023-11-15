package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter. Suppose we can extract JWT and perform Authentication.
//        return (exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//            ServerHttpResponse response = exchange.getResponse();
//
//            log.info("Global Filter baseMessage: {}", config.getBaseMessage());
//
//            if (config.isPreLogger()) {
//                log.info("Global Filter Start: request if -> {}", request.getId());
//            }
//            // Custom Post Filter. Suppose we can call error response handler based on error code.
//            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//                if (config.isPostLogger()) {
//                    log.info("Global Filter End: response if -> {}", response.getStatusCode());
//                }
//            }));
//        };
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter baseMessage: {}", config.getBaseMessage());

            if (config.isPreLogger()) {
                log.info("Logging PRE Filter: request if -> {}", request.getPath());
            }
            // Custom Post Filter. Suppose we can call error response handler based on error code.
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Logging POST Filter: response if -> {}", response.getStatusCode());
                }
            }));
        }, Ordered.LOWEST_PRECEDENCE); // GatewayFilter를 생성해주는 자식 클래스임
        // 두 번째 인자 -> 우선순위를 설정.
        //   - Ordered.HIGHEST_PRECEDENCE: 가장 먼저 실행됨
        //   - Ordered.LOWEST_PRECEDENCE: 가장 마지막에 실행됨

        return filter;
    }

    @Data
    public static class Config {
        // Put the Configuration properties
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
