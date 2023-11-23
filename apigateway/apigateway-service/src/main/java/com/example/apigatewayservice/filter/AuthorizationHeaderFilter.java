package com.example.apigatewayservice.filter;

import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    public static class Config {

    }

    /**
     * 사용자가 요청했을 때 token이 적절히 인증되었는지, 잘 들어가 있는지, 잘 발급되었는지 확인함
     * login -> token -> users (with token) -> header (include token)
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION))
                return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);

            // bearer token,
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", ""); // Bearer 이후에 token이 붙어있는 문자열

            if (!isJwtValid(jwt))
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);

            return chain.filter(exchange);
        };
    }

    /**
     * Spring WebFlux --> Spring 5.x 부터
     * 반환값: Mono, Flux
     * HttpServletRequest, HttpServletResponse 사용하지 않음 -> 대신 ServerHttpRequest, ServerHttpResponse
     */
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete(); // Mono 타입으로 전달
    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;
        String subject = null; // userId (uuid)

        try {
            Key secretKey = Keys.hmacShaKeyFor(env.getProperty("token.secret").getBytes(StandardCharsets.UTF_8));
            JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder().setSigningKey(secretKey);
            subject = jwtParserBuilder.build()
                    .parseClaimsJws(jwt) // parseClaimsJwt( ) 아님
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            log.error("jwtParser = {}", e.getMessage());
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

        return returnValue;
    }
}
