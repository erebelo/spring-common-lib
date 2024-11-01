package com.erebelo.spring.common.http;

import com.erebelo.spring.common.http.support.ConnectionConfiguration;
import com.erebelo.spring.common.http.support.ProxyConfiguration;
import com.erebelo.spring.common.http.support.RequestConfiguration;
import com.erebelo.spring.common.utils.http.HttpTraceHeader;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for setting up HTTP client-related beans. It provides a
 * default RestTemplate and custom RestTemplates based on application
 * properties, enabling HTTP interactions with services.
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.common", name = "http-client.enabled", matchIfMissing = true)
public class HttpClientConfiguration {

    public static final String DEFAULT_REST_TEMPLATE_NAME = "default";

    private final ConfigurableBeanFactory beanFactory;
    private final RestTemplateBuilder restTemplateBuilder;
    private final HttpClientProperties clientConfiguration;

    /**
     * Creates a default RestTemplate bean configured with standard or custom HTTP
     * client settings. This method checks for service properties and applies
     * configurations accordingly.
     *
     * @return a configured RestTemplate instance
     */
    @Primary
    @Bean("RestTemplate")
    public RestTemplate restTemplate() {
        return this.getRestTemplate(
                Objects.requireNonNullElse(clientConfiguration.getServices().get(DEFAULT_REST_TEMPLATE_NAME),
                        new HttpClientProperties.ServiceProperties()));
    }

    /**
     * Registers additional RestTemplate instances for custom services based on
     * configurations defined in application properties. This is done during the
     * post construction phase to ensure all necessary configurations are loaded.
     * Each custom RestTemplate is registered as a singleton bean in the application
     * context.
     */
    @PostConstruct
    public void registerCustomClients() {
        if (clientConfiguration.getServices() != null) {
            for (Map.Entry<String, HttpClientProperties.ServiceProperties> entry : clientConfiguration.getServices()
                    .entrySet()) {
                if (!DEFAULT_REST_TEMPLATE_NAME.equals(entry.getKey())) {
                    RestTemplate restTemplate = this.getRestTemplate(entry.getValue());
                    beanFactory.registerSingleton(String.format("%sRestTemplate", entry.getKey()), restTemplate);
                }
            }
        }
    }

    /**
     * Configures and returns a RestTemplate based on the provided service
     * properties. This method sets up interceptors for trace headers and basic
     * authentication if specified.
     *
     * @param serviceProperties
     *            the properties used to customize the RestTemplate
     * @return a fully configured RestTemplate instance
     */
    private RestTemplate getRestTemplate(HttpClientProperties.ServiceProperties serviceProperties) {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(this.getTraceHeadersInterceptor());

        if (serviceProperties.getAuth() != null) {
            interceptors.add(new BasicAuthenticationInterceptor(serviceProperties.getAuth().getUser(),
                    serviceProperties.getAuth().getPwd()));
        }

        return restTemplateBuilder.interceptors(interceptors)
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient(serviceProperties)))
                .build();
    }

    /**
     * Creates an interceptor that adds trace headers to outgoing HTTP requests.
     * These headers can be used for monitoring or tracing request flow.
     *
     * @return a ClientHttpRequestInterceptor that adds trace headers
     */
    private ClientHttpRequestInterceptor getTraceHeadersInterceptor() {
        return (request, body, execution) -> {
            MultiValueMap<String, String> httpHeaders = HttpTraceHeader
                    .getMultiValueMapDefaultHttpTraceHeaders(HttpTraceHeader.getHttpServletRequest());
            request.getHeaders().addAll(httpHeaders);
            return execution.execute(request, body);
        };
    }

    /**
     * Configures and builds an HttpClient with the necessary connection settings.
     * Cookie management is disabled, as interactions are expected to be stateless.
     * If the service is external, proxy settings are configured accordingly.
     *
     * @param serviceConfig
     *            the service-specific configuration for the HttpClient
     * @return a configured HttpClient instance
     */
    private HttpClient httpClient(HttpClientProperties.ServiceProperties serviceConfig) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setConnectionManager(ConnectionConfiguration.connectionManager()).disableCookieManagement()
                .setDefaultRequestConfig(RequestConfiguration.requestConfig(serviceConfig.getRequest()))
                .useSystemProperties();

        if (serviceConfig.isExternal()) {
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            httpClientBuilder
                    .setProxy(ProxyConfiguration.configProxy(credentialsProvider, clientConfiguration.getProxy()));
        }

        return httpClientBuilder.build();
    }
}
