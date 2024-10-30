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
 * This class provides a singleton bean for a simple {@link RestTemplate}, with
 * default configuration, that supports calls for services in the same
 * infrastructure. It also provides, on post construct phase, a singleton for
 * each custom RestTemplate, given configuration passed through
 * {@link HttpClientProperties}.
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
     * Provides a default {@link RestTemplate} bean with either basic or custom http
     * client configuration.
     */
    @Primary
    @Bean("RestTemplate")
    public RestTemplate restTemplate() {
        return this.getRestTemplate(
                Objects.requireNonNullElse(clientConfiguration.getServices().get(DEFAULT_REST_TEMPLATE_NAME),
                        new HttpClientProperties.ServiceProperties()));
    }

    /**
     * Provides rest templates with custom configuration, defined in application
     * properties.
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

    private ClientHttpRequestInterceptor getTraceHeadersInterceptor() {
        return (request, body, execution) -> {
            MultiValueMap<String, String> httpHeaders = HttpTraceHeader
                    .getMultiValueMapDefaultHttpTraceHeaders(HttpTraceHeader.getHttpServletRequest());
            request.getHeaders().addAll(httpHeaders);
            return execution.execute(request, body);
        };
    }

    /**
     * We disable cookie management, expecting to interact with servers that don’t
     * use cookies. Set system properties for runtime, when you need to override
     * properties set in this config class.
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