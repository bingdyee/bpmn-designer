package io.incubator.wazx.gateway.filter;

import com.google.common.base.Strings;
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
        ServerHttpRequest oldRequest= exchange.getRequest();
        URI uri = oldRequest.getURI();
        ServerHttpRequest  newRequest = oldRequest.mutate().uri(uri).build();
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        List<String> tokenKeys = headers.get(AUTHORIZATION);
        if (tokenKeys != null && tokenKeys.size() > 0) {
            String token = tokenKeys.get(0).substring(BEARER_PREFIX.length());
            String jwt = stringRedisTemplate.opsForValue().get(token);
            if (Strings.isNullOrEmpty(jwt)) {
                return returnAuthFail(exchange, "Err token");
            }
            headers.remove(AUTHORIZATION);
            headers.set(AUTHORIZATION, BEARER_PREFIX + jwt);
        } else {
            return returnAuthFail(exchange, "Empty token");
        }
        newRequest = new ServerHttpRequestDecorator(newRequest) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(headers);
                return httpHeaders;
            }
        };
        return chain.filter(exchange.mutate().request(newRequest).build());
    }



    private Mono<Void> returnAuthFail(ServerWebExchange exchange,String message) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        serverHttpResponse.getHeaders().clearContentHeaders();
        serverHttpResponse.getHeaders().add("ContentType", "application/json");
        String resultData = "{\"status\":\"-1\",\"msg\":"+message+"}";
        byte[] bytes = resultData.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
