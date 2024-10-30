package com.erebelo.spring.common.http.support;

import lombok.Data;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;

public class RequestConfiguration {

    public static RequestConfig requestConfig(Properties properties) {
        return RequestConfig.custom().setConnectionRequestTimeout(Timeout.ofMilliseconds(properties.connTimeout))
                .setResponseTimeout(Timeout.ofMilliseconds(properties.connReadTimeout)).build();
    }

    @Data
    public static class Properties {
        private long connTimeout = 3000L;
        private long connReadTimeout = 5000L;
    }
}
