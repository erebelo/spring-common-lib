package com.erebelo.spring.common.http;

import com.erebelo.spring.common.http.support.ProxyConfiguration;
import com.erebelo.spring.common.http.support.RequestConfiguration;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.common.http-client")
public class HttpClientProperties {

    private Map<String, ServiceProperties> services = new HashMap<>();
    private ProxyConfiguration.Properties proxy = new ProxyConfiguration.Properties();

    @Data
    public static class ServiceProperties {
        private boolean external;
        private String baseUrl;
        private ProxyConfiguration.Properties auth;
        private RequestConfiguration.Properties request = new RequestConfiguration.Properties();
    }
}
