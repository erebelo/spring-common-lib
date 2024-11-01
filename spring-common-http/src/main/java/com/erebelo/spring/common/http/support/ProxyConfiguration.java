package com.erebelo.spring.common.http.support;

import com.erebelo.spring.common.http.HttpClientProperties;
import java.net.URI;
import lombok.experimental.UtilityClass;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;

/**
 * Utility class for configuring HTTP proxy settings. It provides methods to set
 * up proxy authentication and create HttpHost instances for proxy connections.
 */
@UtilityClass
public class ProxyConfiguration {

    /**
     * Configures a proxy using the provided credentials provider and proxy
     * properties. This method delegates to the configAuth method to handle
     * authentication.
     *
     * @param credentialsProvider
     *            the credentials provider for proxy authentication
     * @param proxyProperties
     *            the properties that define the proxy settings
     * @return an HttpHost representing the configured proxy, or null if not
     *         configured
     */
    public static HttpHost configProxy(BasicCredentialsProvider credentialsProvider,
            HttpClientProperties.ProxyProperties proxyProperties) {
        return configAuth(credentialsProvider, proxyProperties);
    }

    /**
     * Configures authentication for the proxy based on the provided properties. If
     * valid proxy properties are supplied, it creates an HttpHost and sets the
     * necessary credentials for proxy access.
     *
     * @param credentialsProvider
     *            the credentials provider to store proxy credentials
     * @param proxyProperties
     *            the properties containing proxy configuration details
     * @return an HttpHost representing the proxy host, or null if proxy properties
     *         are incomplete
     */
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
