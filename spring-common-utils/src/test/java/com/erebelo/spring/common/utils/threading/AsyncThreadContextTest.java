package com.erebelo.spring.common.utils.threading;

import com.erebelo.spring.common.utils.http.HttpTraceHeader;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class AsyncThreadContextTest {

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
    void testWithThreadContextRunnable() {
        try (MockedStatic<HttpTraceHeader> mockedStatic = mockStatic(HttpTraceHeader.class)) {
            mockedStatic.when(HttpTraceHeader::getHttpServletRequest).thenReturn(mock(HttpServletRequest.class));
            mockedStatic.when(() -> HttpTraceHeader.getDefaultHttpTraceHeaders(any())).thenReturn(HTTP_HEADERS);

            Runnable runnable = () -> assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));

            AsyncThreadContext.withThreadContext(runnable).run();

            assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));
        }
    }

    @Test
    void testWithThreadContextSupplier() {
        try (MockedStatic<HttpTraceHeader> mockedStatic = mockStatic(HttpTraceHeader.class)) {
            mockedStatic.when(HttpTraceHeader::getHttpServletRequest).thenReturn(mock(HttpServletRequest.class));
            mockedStatic.when(() -> HttpTraceHeader.getDefaultHttpTraceHeaders(any())).thenReturn(HTTP_HEADERS);

            Supplier<String> supplier = () -> {
                assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));
                return "Some Value";
            };

            String result = AsyncThreadContext.withThreadContext(supplier).get();

            assertEquals("Some Value", result);
            assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));
        }
    }
}
