package com.erebelo.spring.common.utils.threading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.erebelo.spring.common.utils.http.HeaderContextHolder;
import com.erebelo.spring.common.utils.http.HttpTraceHeader;
import java.util.Map;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

class AsyncExecutorConfigurationTest {

    private static final String HEADER_KEY = "Header1";
    private static final String HEADER_VALUE = "Value1";
    private static final Map<String, String> HTTP_HEADERS = Map.of(HEADER_KEY, HEADER_VALUE);

    @Test
    void testAsyncTaskExecutorConfiguration() {
        AsyncExecutorConfiguration config = new AsyncExecutorConfiguration();
        ThreadPoolTaskExecutor executor = config.asyncTaskExecutor();

        assertEquals(10, executor.getCorePoolSize());
        assertEquals(20, executor.getMaxPoolSize());
        assertEquals(500, executor.getQueueCapacity());
        assertEquals("AsyncThread-", executor.getThreadNamePrefix());
    }

    @Test
    void testContextCopyingTaskDecoratorRunnable() {
        try (MockedStatic<HttpTraceHeader> mockedStatic = mockStatic(HttpTraceHeader.class)) {
            RequestAttributes mockRequestAttributes = mock(RequestAttributes.class);

            mockedStatic.when(HttpTraceHeader::getRequestAttributes).thenReturn(mockRequestAttributes);
            mockedStatic.when(() -> HttpTraceHeader.getDefaultHttpTraceHeaders(any())).thenReturn(HTTP_HEADERS);

            AsyncExecutorConfiguration.ContextCopyingTaskDecorator decorator = new AsyncExecutorConfiguration.ContextCopyingTaskDecorator();

            Runnable task = () -> {
                assertEquals(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
                assertEquals(HTTP_HEADERS, HeaderContextHolder.get());
                assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));
            };

            Runnable decoratedTask = decorator.decorate(task);
            decoratedTask.run();

            assertNotSame(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
            assertTrue(HeaderContextHolder.get().isEmpty());
            assertNull(ThreadContext.get(HEADER_KEY));
        }
    }
}
