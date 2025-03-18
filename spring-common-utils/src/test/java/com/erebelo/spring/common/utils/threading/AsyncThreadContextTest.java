package com.erebelo.spring.common.utils.threading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.erebelo.spring.common.utils.http.HttpTraceHeader;
import jakarta.servlet.http.HttpServletRequest;
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
            mockedStatic.when(HttpTraceHeader::getHttpServletRequest).thenReturn(mock(HttpServletRequest.class));
            mockedStatic.when(() -> HttpTraceHeader.getDefaultHttpTraceHeaders(any())).thenReturn(HTTP_HEADERS);
            mockedStatic.when(HttpTraceHeader::getRequestAttributes).thenReturn(mockRequestAttributes);

            Runnable runnable = () -> {
                assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));
                assertEquals(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
            };

            AsyncThreadContext.withThreadContext(runnable).run();

            assertNotSame(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
            assertNull(ThreadContext.get(HEADER_KEY));
        }
    }

    @Test
    void testWithThreadContextSupplier() {
        try (MockedStatic<HttpTraceHeader> mockedStatic = mockStatic(HttpTraceHeader.class)) {
            mockedStatic.when(HttpTraceHeader::getHttpServletRequest).thenReturn(mock(HttpServletRequest.class));
            mockedStatic.when(() -> HttpTraceHeader.getDefaultHttpTraceHeaders(any())).thenReturn(HTTP_HEADERS);
            mockedStatic.when(HttpTraceHeader::getRequestAttributes).thenReturn(mockRequestAttributes);

            Supplier<String> supplier = () -> {
                assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));
                assertEquals(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
                return "Some Value";
            };

            String response = AsyncThreadContext.withThreadContext(supplier).get();

            assertEquals("Some Value", response);
            assertNotSame(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
            assertNull(ThreadContext.get(HEADER_KEY));
        }
    }
}
