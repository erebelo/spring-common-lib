package com.erebelo.spring.common.http.support;

import com.erebelo.spring.common.http.HttpClientProperties;
import java.net.URI;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;

public class ProxyConfiguration {

    public static HttpHost configProxy(BasicCredentialsProvider credentialsProvider,
            ProxyConfiguration.Properties properties) {
        ProxyConfiguration.Properties proxyProperties = new ProxyConfiguration.Properties(properties.host,
                properties.port, properties.user, properties.pwd);
        return configAuth(credentialsProvider, proxyProperties);
    }

    private static HttpHost configAuth(BasicCredentialsProvider credentialsProvider,
            ProxyConfiguration.Properties properties) {
        if (properties.host == null || properties.user == null || properties.pwd == null) {
            return null;
        }

        URI uri = URI.create(properties.host);
        HttpHost host = new HttpHost(uri.getScheme(), Objects.requireNonNull(uri.getHost()),
                uri.getPort() > 0 ? uri.getPort() : 80);

        credentialsProvider.setCredentials(new AuthScope(host),
                new UsernamePasswordCredentials(properties.user, properties.pwd.toCharArray()));

        return host;
    }

    private static void configAuth(BasicCredentialsProvider credentialsProvider,
            HttpClientProperties.ServiceProperties service) {
        if (service == null || service.getAuth() == null || service.getAuth().getHost() == null) {
            return;
        }

        service.getAuth().setHost(service.getBaseUrl());
        configAuth(credentialsProvider, service.getAuth());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Properties {
        private String host;
        private Integer port;
        private String user;
        private String pwd;

        public Properties(String host, Integer port) {
            this.host = host;
            this.port = port;
        }
    }
}
