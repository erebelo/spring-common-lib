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
import java.util.function.Supplier;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

class AsyncThreadContextTest {

    private RequestAttributes mockRequestAttributes;

    private static final String HEADER_KEY = "Header1";
    private static final String HEADER_VALUE = "Value1";
    private static final Map<String, String> HTTP_HEADERS = Map.of(HEADER_KEY, HEADER_VALUE);

    @BeforeEach
    void setUp() {
        mockRequestAttributes = mock(RequestAttributes.class);
    }

    @Test
    void testWithThreadContextRunnable() {
        try (MockedStatic<HttpTraceHeader> mockedStatic = mockStatic(HttpTraceHeader.class)) {
            mockedStatic.when(HttpTraceHeader::getRequestAttributes).thenReturn(mockRequestAttributes);
            mockedStatic.when(() -> HttpTraceHeader.getDefaultHttpTraceHeaders(any())).thenReturn(HTTP_HEADERS);

            Runnable runnable = () -> {
                assertEquals(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
                assertEquals(HTTP_HEADERS, HeaderContextHolder.get());
                assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));
            };

            AsyncThreadContext.withThreadContext(runnable).run();

            assertNotSame(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
            assertTrue(HeaderContextHolder.get().isEmpty());
            assertNull(ThreadContext.get(HEADER_KEY));
        }
    }

    @Test
    void testWithThreadContextSupplier() {
        try (MockedStatic<HttpTraceHeader> mockedStatic = mockStatic(HttpTraceHeader.class)) {
            mockedStatic.when(HttpTraceHeader::getRequestAttributes).thenReturn(mockRequestAttributes);
            mockedStatic.when(() -> HttpTraceHeader.getDefaultHttpTraceHeaders(any())).thenReturn(HTTP_HEADERS);

            Supplier<String> supplier = () -> {
                assertEquals(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
                assertEquals(HTTP_HEADERS, HeaderContextHolder.get());
                assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));
                return "Some Value";
            };

            String response = AsyncThreadContext.withThreadContext(supplier).get();

            assertEquals("Some Value", response);
            assertNotSame(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
            assertTrue(HeaderContextHolder.get().isEmpty());
            assertNull(ThreadContext.get(HEADER_KEY));
        }
    }
}
