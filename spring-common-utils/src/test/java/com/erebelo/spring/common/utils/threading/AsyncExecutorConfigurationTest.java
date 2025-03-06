package com.erebelo.spring.common.utils.threading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.erebelo.spring.common.utils.http.HttpTraceHeader;
import java.util.Map;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.ServletRequestAttributes;

class AsyncExecutorConfigurationTest {

    private static final String HEADER_KEY = "Header1";
    private static final String HEADER_VALUE = "Value1";
    private static final Map<String, String> HTTP_HEADERS = Map.of(HEADER_KEY, HEADER_VALUE);

    @BeforeEach
    void setUp() {
        ThreadContext.clearAll();
    }

    @AfterEach
    void tearDown() {
        ThreadContext.clearAll();
    }

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
            ServletRequestAttributes requestAttributesMock = mock(ServletRequestAttributes.class);

            mockedStatic.when(HttpTraceHeader::getRequestAttributes).thenReturn(requestAttributesMock);
            mockedStatic.when(() -> HttpTraceHeader.getDefaultHttpTraceHeaders(any())).thenReturn(HTTP_HEADERS);

            ThreadContext.put(HEADER_KEY, HEADER_VALUE);

            AsyncExecutorConfiguration.ContextCopyingTaskDecorator decorator = new AsyncExecutorConfiguration.ContextCopyingTaskDecorator();

            Runnable task = () -> assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));

            Runnable decoratedTask = decorator.decorate(task);
            decoratedTask.run();

            assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));
        }
    }
}
