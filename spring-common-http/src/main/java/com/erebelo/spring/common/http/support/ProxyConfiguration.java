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
            HttpClientProperties.ProxyProperties proxyProperties) {
        return configAuth(credentialsProvider, proxyProperties);
    }

    private static HttpHost configAuth(BasicCredentialsProvider credentialsProvider,
            HttpClientProperties.ProxyProperties proxyProperties) {
        if (proxyProperties.getHost() != null && proxyProperties.getUser() != null
                && proxyProperties.getPwd() != null) {
            String scheme = (proxyProperties.getPort() != null && proxyProperties.getPort() == 443) ? "https" : "http";
            String uriString = scheme + "://" + proxyProperties.getHost()
                    + (proxyProperties.getPort() != null ? ":" + proxyProperties.getPort() : "");

            URI uri = URI.create(uriString);
            HttpHost httpHost = new HttpHost(scheme, uri.getHost(),
                    proxyProperties.getPort() != null ? proxyProperties.getPort() : (scheme.equals("http") ? 80 : 443));

            credentialsProvider.setCredentials(new AuthScope(httpHost),
                    new UsernamePasswordCredentials(proxyProperties.getUser(), proxyProperties.getPwd().toCharArray()));

            return httpHost;
        }

        return null;
    }
}
