package com.erebelo.spring.common.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class HttpClientConfigurationTest {

    private HttpClientConfiguration httpClientConfig;
    private final ConfigurableBeanFactory beanFactory = new DefaultListableBeanFactory();
    private final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    @BeforeEach
    void setup() throws IOException {
        Properties properties = PropertiesLoaderUtils
                .loadProperties(new ClassPathResource("application-test" + ".properties"));
        Map<String, Object> propertiesMap = properties.entrySet().stream()
                .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), Map.Entry::getValue));

        // Assert the file was found and loaded
        assertEquals(9, propertiesMap.size());
        Binder binder = new Binder(
                ConfigurationPropertySources.from(new MapPropertySource("properties", propertiesMap)));
        HttpClientProperties httpClientProperties = binder
                .bind("spring.common.http-client", Bindable.of(HttpClientProperties.class)).get();

        // Assert the properties values were loaded into the model
        assertNotNull(httpClientProperties);

        // Instantiate HttpClientConfiguration with dependencies
        httpClientConfig = new HttpClientConfiguration(beanFactory, restTemplateBuilder, httpClientProperties);
    }

    @Test
    void testPrimaryRestTemplateCreation() throws IllegalAccessException {
        RestTemplate restTemplate = httpClientConfig.restTemplate();
        assertNotNull(restTemplate);
        assertNotNull(restTemplate.getInterceptors());
        assertEquals(1, restTemplate.getInterceptors().size());

        HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) ((InterceptingClientHttpRequestFactory) restTemplate
                .getRequestFactory()).getDelegate();
        HttpClient httpclient = requestFactory.getHttpClient();

        RequestConfig requestConfig = (RequestConfig) FieldUtils
                .getDeclaredField(httpclient.getClass(), "defaultConfig", true).get(httpclient);
        assertEquals(7000, requestConfig.getConnectionRequestTimeout().toMilliseconds());
        assertEquals(10000, requestConfig.getResponseTimeout().toMilliseconds());
        assertEquals(50, requestConfig.getMaxRedirects());
        assertEquals(3, requestConfig.getConnectionKeepAlive().toMinutes());

        DefaultProxyRoutePlanner routePlanner = (DefaultProxyRoutePlanner) FieldUtils
                .getDeclaredField(httpclient.getClass(), "routePlanner", true).get(httpclient);
        HttpHost httpHost = (HttpHost) FieldUtils.getDeclaredField(routePlanner.getClass(), "proxy", true)
                .get(routePlanner);
        assertEquals("http", httpHost.getSchemeName());
        assertEquals("localhost", httpHost.getHostName());
        assertEquals(8080, httpHost.getPort());
    }

    @Test
    void testAdditionalRestTemplatesCreation() {
        httpClientConfig.registerCustomClients();

        RestTemplate serviceTwoRestTemplate = (RestTemplate) beanFactory.getBean("serviceTwoRestTemplate");
        assertNotNull(serviceTwoRestTemplate);
        assertEquals(2, serviceTwoRestTemplate.getInterceptors().size());
    }
}
