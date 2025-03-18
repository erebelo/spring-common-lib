package com.erebelo.spring.common.utils.threading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.erebelo.spring.common.utils.http.HeaderContextHolder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

class ParallelStreamContextTest {

    private static final String HEADER_KEY = "Header1";
    private static final String HEADER_VALUE = "Value1";
    private static final Map<String, String> LOGGING_CONTEXT = Map.of(HEADER_KEY, HEADER_VALUE);

    @Test
    void testForEachPreservesContext() {
        try (MockedStatic<RequestContextHolder> requestContextMock = mockStatic(RequestContextHolder.class);
                MockedStatic<HeaderContextHolder> headerContextMock = mockStatic(HeaderContextHolder.class)) {
            RequestAttributes mockRequestAttributes = mock(RequestAttributes.class);

            requestContextMock.when(RequestContextHolder::getRequestAttributes).thenReturn(mockRequestAttributes);
            headerContextMock.when(HeaderContextHolder::get).thenReturn(LOGGING_CONTEXT);
            headerContextMock.when(HeaderContextHolder::isPresent).thenReturn(false);

            List<String> results = new CopyOnWriteArrayList<>();
            Stream<String> stream = Stream.of("A", "B", "C");

            ParallelStreamContext.forEach(stream, item -> {
                assertEquals(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
                assertEquals(LOGGING_CONTEXT, HeaderContextHolder.get());
                assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));
                results.add(item);
            });

            assertEquals(3, results.size());
            assertTrue(results.containsAll(List.of("A", "B", "C")));

            assertEquals(mockRequestAttributes, RequestContextHolder.getRequestAttributes());
            assertEquals(LOGGING_CONTEXT, HeaderContextHolder.get());
            assertEquals(HEADER_VALUE, ThreadContext.get(HEADER_KEY));
        }
    }
}
