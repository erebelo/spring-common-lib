package com.erebelo.spring.common.http.support;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import lombok.experimental.UtilityClass;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContexts;

/**
 * Utility class for configuring HTTP connection settings. It provides methods
 * to set up connection management and TLS socket strategy for secure
 * connections.
 */
@UtilityClass
public class ConnectionConfiguration {

    /**
     * Creates a pooling HTTP client connection manager with default connection
     * configurations. It sets up a TLS socket strategy using the configured SSL
     * context for secure communication.
     *
     * @return a PoolingHttpClientConnectionManager for managing HTTP connections
     */
    public static PoolingHttpClientConnectionManager connectionManager() {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(ConnectionConfig.custom().build())
                .setTlsSocketStrategy(createTlsSocketStrategy()).build();
    }

    /**
     * Configures the SSL context for secure connections. This method allows all
     * certificates by loading trust material that accepts any X509 certificates.
     *
     * @return an SSLContext configured for secure connections
     * @throws RuntimeException
     *             if an error occurs during SSL context configuration
     */
    private static TlsSocketStrategy createTlsSocketStrategy() {
        try {
            // Create an SSLContext that allows any certificate
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((x509Certificates, s) -> true).build();

            return (TlsSocketStrategy) ClientTlsStrategyBuilder.create().setSslContext(sslContext)
                    .setTlsVersions(TLS.V_1_2, TLS.V_1_3).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new IllegalStateException("Failed to configure SSLContext", e);
        }
    }
}
