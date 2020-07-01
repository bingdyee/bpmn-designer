package io.incubator.wazx.oauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author : yubb
 * @date : 2019-08-20
 */
@Service
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * cache code to redis，Expiration:10min <br>
     * value: OAuth2Authentication serialize byte<br>
     */
    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        redisTemplate.execute((RedisCallback<Long>) connection -> {
            connection.set(codeKey(code).getBytes(), SerializationUtils.serialize(authentication),
                    Expiration.from(10, TimeUnit.MINUTES), RedisStringCommands.SetOption.UPSERT);
            return 1L;
        });
    }

    @Override
    protected OAuth2Authentication remove(final String code) {
        return redisTemplate.execute((RedisCallback<OAuth2Authentication>) connection -> {
            byte[] keyByte = codeKey(code).getBytes();
            byte[] valueByte = connection.get(keyByte);
            if (valueByte != null) {
                connection.del(keyByte);
                return SerializationUtils.deserialize(valueByte);
            }
            return null;
        });
    }

    /**
     * cache key prefix
     *
     * @param code
     */
    private String codeKey(String code) {
        return "oauth2:codes:" + code;
    }

}
