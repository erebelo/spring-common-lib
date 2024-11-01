package com.erebelo.spring.common.http;

import com.erebelo.spring.common.http.support.RequestConfiguration;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the HTTP client. This class holds settings
 * related to HTTP services, authentication, and proxy configuration. It is used
 * to configure custom settings over application properties using the prefix
 * 'spring.common.http-client'. This allows for easy modification and management
 * of HTTP client settings through the application configuration file.
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.common.http-client")
public class HttpClientProperties {

    // A map of service-specific properties, allowing for configuration of multiple
    // HTTP services
    private Map<String, ServiceProperties> services = new HashMap<>();

    // Properties related to proxy configuration for HTTP requests.
    private ProxyProperties proxy = new ProxyProperties();

    /**
     * Nested class representing properties for each HTTP service.
     */
    @Data
    public static class ServiceProperties {
        private boolean external;
        private AuthProperties auth;
        private RequestConfiguration.Properties request = new RequestConfiguration.Properties();
    }

    /**
     * Nested class representing authentication properties.
     */
    @Data
    public static class AuthProperties {
        private String user;
        private String pwd;
    }

    /**
     * Nested class representing proxy configuration properties.
     */
    @Data
    public static class ProxyProperties {
        private String host;
        private Integer port;
        private String user;
        private String pwd;
    }
}
