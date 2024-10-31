package com.erebelo.spring.common.http;

import com.erebelo.spring.common.http.support.RequestConfiguration;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.common.http-client")
public class HttpClientProperties {

    private Map<String, ServiceProperties> services = new HashMap<>();
    private CredentialsProperties proxy = new CredentialsProperties();

    @Data
    public static class ServiceProperties {
        private boolean external;
        private CredentialsProperties auth;
        private RequestConfiguration.Properties request = new RequestConfiguration.Properties();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CredentialsProperties {
        private String host;
        private Integer port;
        private String user;
        private String pwd;

        public CredentialsProperties(String host, Integer port) {
            this.host = host;
            this.port = port;
        }
    }
}
