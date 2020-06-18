package io.incubator.wazx.gateway.filter;

import com.google.common.base.Strings;
import io.incubator.common.pojo.ResponseEntity;
import net.sf.cglib.core.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * @author Noa Swartz
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";

    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest originalRequest = exchange.getRequest();
        HttpHeaders headers = originalRequest.getHeaders();
        List<String> tokenKeys = headers.get(AUTHORIZATION);
        if (tokenKeys != null && tokenKeys.size() > 0) {
            String token = tokenKeys.get(0).substring(BEARER_PREFIX.length());
            String jwt = stringRedisTemplate.opsForValue().get(token);
            if (Strings.isNullOrEmpty(jwt)) {
                return unauthorized(exchange, "invalid token");
            }
            ServerHttpRequest modifiedRequest = exchange
                    .getRequest()
                    .mutate()
                    .headers(h -> h.set(AUTHORIZATION, BEARER_PREFIX + jwt))
                    .build();
            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
            return chain.filter(modifiedExchange);
        }
        return chain.filter(exchange);
    }



    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        serverHttpResponse.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        byte[] bytes = ResponseEntity.of(HttpStatus.UNAUTHORIZED, message).toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
