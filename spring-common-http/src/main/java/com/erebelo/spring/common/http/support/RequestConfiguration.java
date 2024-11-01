package com.erebelo.spring.common.http.support;

import lombok.Data;
import lombok.experimental.UtilityClass;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;

/**
 * Utility class for configuring HTTP request settings. It provides methods to
 * create custom RequestConfig instances based on specified timeout properties.
 */
@UtilityClass
public class RequestConfiguration {

    /**
     * Creates a RequestConfig instance with custom timeout settings. The
     * configuration includes connection request timeout and response timeout based
     * on the provided properties.
     *
     * @param properties
     *            the timeout settings to apply to the request configuration
     * @return a configured RequestConfig instance
     */
    public static RequestConfig requestConfig(Properties properties) {
        return RequestConfig.custom().setConnectionRequestTimeout(Timeout.ofMilliseconds(properties.connTimeout))
                .setResponseTimeout(Timeout.ofMilliseconds(properties.connReadTimeout)).build();
    }

    /**
     * A data class that holds timeout properties for HTTP requests. It contains
     * default values for connection timeout and read timeout, which can be
     * customized as needed.
     */
    @Data
    public static class Properties {
        private long connTimeout = 3000L;
        private long connReadTimeout = 5000L;
    }
}
