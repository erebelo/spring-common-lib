package com.erebelo.spring.common.http.support;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import lombok.experimental.UtilityClass;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;

/**
 * Utility class for configuring HTTP connection settings. It provides methods
 * to set up connection management and SSL context for secure connections.
 */
@UtilityClass
public class ConnectionConfiguration {

    /**
     * Creates a pooling HTTP client connection manager with default connection
     * configurations. It sets up an SSL socket factory using the configured SSL
     * context for secure communication.
     *
     * @return a PoolingHttpClientConnectionManager for managing HTTP connections
     */
    public static PoolingHttpClientConnectionManager connectionManager() {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(ConnectionConfig.custom().build())
                .setSSLSocketFactory(new SSLConnectionSocketFactory(configSslContext())).build();
    }

    /**
     * Configures the SSL context for secure connections. This method allows all
     * certificates by loading trust material that accepts any X509 certificates.
     *
     * @return an SSLContext configured for secure connections
     * @throws RuntimeException
     *             if an error occurs during SSL context configuration
     */
    private static SSLContext configSslContext() {
        try {
            return SSLContexts.custom().loadTrustMaterial((x509Certificates, s) -> true).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }
}
