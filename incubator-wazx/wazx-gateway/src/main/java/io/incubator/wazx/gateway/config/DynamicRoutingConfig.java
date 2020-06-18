package io.incubator.wazx.gateway.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author Noa Swartz
 */
@Component
public class DynamicRoutingConfig implements ApplicationEventPublisherAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicRoutingConfig.class);

    private static final String DATA_ID = "gateway-routes";
    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr;
    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String namespace;

    private static String ROUTES = "";

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void autoRefreshRouts() throws NacosException {
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace", namespace);
        ConfigService configService = NacosFactory.createConfigService(properties);
        String routesConfig = configService.getConfig(DATA_ID, DEFAULT_GROUP, 5000);
        if (!Strings.isNullOrEmpty(routesConfig)) {
            refreshRouts(routesConfig);
        }
        configService.addListener(DATA_ID, DEFAULT_GROUP, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                if (!configInfo.equals(ROUTES)) {
                    refreshRouts(configInfo);
                }
            }
            @Override
            public Executor getExecutor() {
                return null;
            }
        });
    }

    private void refreshRouts(String configInfo) {
        LOGGER.info("Refresh Routes...");
        List<RouteDefinition> gatewayRouteDefinitions = JSONObject.parseArray(configInfo, RouteDefinition.class);
        for (RouteDefinition routeDefinition : gatewayRouteDefinitions) {
            try {
                routeDefinitionWriter.delete(Mono.just(routeDefinition.getId())).subscribe();
            } catch (Exception ignored){ }
            try {
                routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
            } catch (Exception ignored){ }
        }
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(routeDefinitionWriter));
        ROUTES = configInfo;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

}
