package io.incubator.wazx.oauth.security;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author : yubb
 * @date : 2019-08-20
 */
//@Service
public class RedisClientDetailsService extends JdbcClientDetailsService {

    /** client detail cache key，as hash */
    private static final String CACHE_CLIENT_KEY = "client_details";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public RedisClientDetailsService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
        ClientDetails clientDetails;
        // from cache
        String value = (String) stringRedisTemplate.boundHashOps(CACHE_CLIENT_KEY).get(clientId);
        if (Strings.isBlank(value)) {
            clientDetails = cacheAndGetClient(clientId);
        } else {
            clientDetails = JSONObject.parseObject(value, BaseClientDetails.class);
        }
        return clientDetails;
    }

    /**
     * cache client and return client
     *
     * @param clientId
     */
    private ClientDetails cacheAndGetClient(String clientId) {
        // load client from database
        ClientDetails clientDetails = super.loadClientByClientId(clientId);
        // write to redis
        if (clientDetails != null) {
            stringRedisTemplate.boundHashOps(CACHE_CLIENT_KEY).put(clientId, JSONObject.toJSONString(clientDetails));
        }
        return clientDetails;
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        super.updateClientDetails(clientDetails);
        cacheAndGetClient(clientDetails.getClientId());
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        super.updateClientSecret(clientId, secret);
        cacheAndGetClient(clientId);
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        super.removeClientDetails(clientId);
        removeRedisCache(clientId);
    }

    /**
     * delete redis cache
     *
     * @param clientId
     */
    private void removeRedisCache(String clientId) {
        stringRedisTemplate.boundHashOps(CACHE_CLIENT_KEY).delete(clientId);
    }

    /**
     * load oauth_client_details data to redis
     */
    public void loadAllClientToCache() {
        if (stringRedisTemplate.hasKey(CACHE_CLIENT_KEY)) {
            return;
        }
        List<ClientDetails> list = super.listClientDetails();
        if (!CollectionUtils.isEmpty(list)) {
            list.parallelStream().forEach(client -> {
                stringRedisTemplate.boundHashOps(CACHE_CLIENT_KEY).put(client.getClientId(), JSONObject.toJSONString(client));
            });
        }
    }

}
