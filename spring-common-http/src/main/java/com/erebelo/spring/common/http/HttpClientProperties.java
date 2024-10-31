package com.erebelo.spring.common.http;

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
    private ProxyProperties proxy = new ProxyProperties();

    @Data
    public static class ServiceProperties {
        private boolean external;
        private AuthProperties auth;
        private RequestConfiguration.Properties request = new RequestConfiguration.Properties();
    }

    @Data
    public static class AuthProperties {
        private String user;
        private String pwd;
    }

    @Data
    public static class ProxyProperties {
        private String host;
        private Integer port;
        private String user;
        private String pwd;
    }
}
