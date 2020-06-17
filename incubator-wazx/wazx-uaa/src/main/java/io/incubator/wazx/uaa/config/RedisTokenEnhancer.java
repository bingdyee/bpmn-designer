package io.incubator.wazx.uaa.config;

import com.google.common.base.Joiner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * store jwt to redis for gateway
 *
 * @author Noa Swartz
 */
@Component
public class RedisTokenEnhancer implements TokenEnhancer {

    private static final String MICRO_SVC = "micro_svc";
    private static final String CLIENT_CREDENTIALS = "client_credentials";

    private final StringRedisTemplate stringRedisTemplate;


    public RedisTokenEnhancer(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String grantType = authentication.getOAuth2Request().getGrantType();
        String scope = Joiner.on(",").join(accessToken.getScope());
        if (CLIENT_CREDENTIALS.equals(grantType) && MICRO_SVC.equals(scope)) {
            return accessToken;
        }
        String key = UUID.randomUUID().toString();
        String refreshKey = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(key, accessToken.getValue(), accessToken.getExpiresIn(), TimeUnit.SECONDS);
        Map<String, String> payload = new HashMap<>(16);
        payload.put(OAuth2AccessToken.ACCESS_TOKEN, key);
        payload.put(OAuth2AccessToken.REFRESH_TOKEN, refreshKey);
        payload.put(OAuth2AccessToken.EXPIRES_IN, accessToken.getExpiresIn() + "");
        payload.put(OAuth2AccessToken.SCOPE, scope);
        return DefaultOAuth2AccessToken.valueOf(payload);
    }
}
