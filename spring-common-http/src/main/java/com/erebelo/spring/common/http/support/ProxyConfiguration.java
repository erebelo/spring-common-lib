package com.erebelo.spring.common.http.support;

import com.erebelo.spring.common.http.HttpClientProperties;
import java.net.URI;
import lombok.experimental.UtilityClass;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;

@UtilityClass
public class ProxyConfiguration {

    public static HttpHost configProxy(BasicCredentialsProvider credentialsProvider,
            HttpClientProperties.CredentialsProperties properties) {
        HttpClientProperties.CredentialsProperties proxyProperties = new HttpClientProperties.CredentialsProperties(
                properties.getHost(), properties.getPort(), properties.getUser(), properties.getPwd());
        return configAuth(credentialsProvider, proxyProperties);
    }

    private static HttpHost configAuth(BasicCredentialsProvider credentialsProvider,
            HttpClientProperties.CredentialsProperties properties) {
        if (properties.getHost() != null && properties.getUser() != null && properties.getPwd() != null) {
            String scheme = (properties.getPort() != null && properties.getPort() == 443) ? "https" : "http";
            String uriString = scheme + "://" + properties.getHost()
                    + (properties.getPort() != null ? ":" + properties.getPort() : "");

            URI uri = URI.create(uriString);
            HttpHost httpHost = new HttpHost(scheme, uri.getHost(),
                    properties.getPort() != null ? properties.getPort() : (scheme.equals("http") ? 80 : 443));

            credentialsProvider.setCredentials(new AuthScope(httpHost),
                    new UsernamePasswordCredentials(properties.getUser(), properties.getPwd().toCharArray()));

            return httpHost;
        }

        return null;
    }
}
