package com.erebelo.spring.common.logging;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.erebelo.spring.common.utils.HeaderContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ThreadContextFilterTest {

    @InjectMocks
    private ThreadContextFilter loggingFilter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest servletRequestMock;

    @Mock
    private HttpServletResponse servletResponseMock;

    private static final String REQUEST_ID_HEADER = "RequestID";
    private static final String REQUEST_ID_HEADER_PREFIX = "GEN-";

    @Test
    void testDoFilterInternalWithRequestIdHeader() throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        Map<String, String> headers = Map.of(REQUEST_ID_HEADER, requestId);

        given(servletRequestMock.getHeader(REQUEST_ID_HEADER)).willReturn(requestId);

        try (MockedStatic<ThreadContext> threadContextMockedStatic = mockStatic(ThreadContext.class);
                MockedStatic<HeaderContextHolder> headerContextHolderMockedStatic = mockStatic(
                        HeaderContextHolder.class)) {

            headerContextHolderMockedStatic.when(HeaderContextHolder::isPresent).thenReturn(false);
            headerContextHolderMockedStatic.when(HeaderContextHolder::get).thenReturn(headers);
            headerContextHolderMockedStatic.when(() -> HeaderContextHolder.set(headers)).thenAnswer(invocation -> null);

            loggingFilter.doFilter(servletRequestMock, servletResponseMock, filterChain);

            threadContextMockedStatic.verify(() -> ThreadContext.put(REQUEST_ID_HEADER, requestId));
            verify(filterChain).doFilter(servletRequestMock, servletResponseMock);
            headerContextHolderMockedStatic.verify(HeaderContextHolder::isPresent);
            headerContextHolderMockedStatic.verify(() -> HeaderContextHolder.set(headers));
            headerContextHolderMockedStatic.verify(HeaderContextHolder::get);
            headerContextHolderMockedStatic.verify(HeaderContextHolder::remove);
            threadContextMockedStatic.verify(ThreadContext::clearMap);
        }
    }

    @Test
    void testDoFilterInternalWithoutRequestIdHeader() throws ServletException, IOException {
        UUID requestId = UUID.randomUUID();
        Map<String, String> headers = Map.of(REQUEST_ID_HEADER, REQUEST_ID_HEADER_PREFIX + requestId);

        try (MockedStatic<UUID> uuidMockedStatic = mockStatic(UUID.class);
                MockedStatic<ThreadContext> threadContextMockedStatic = mockStatic(ThreadContext.class);
                MockedStatic<HeaderContextHolder> headerContextHolderMockedStatic = mockStatic(
                        HeaderContextHolder.class)) {

            uuidMockedStatic.when(UUID::randomUUID).thenReturn(requestId);
            headerContextHolderMockedStatic.when(HeaderContextHolder::isPresent).thenReturn(false);
            headerContextHolderMockedStatic.when(HeaderContextHolder::get).thenReturn(headers);
            headerContextHolderMockedStatic.when(() -> HeaderContextHolder.set(headers)).thenAnswer(invocation -> null);

            threadContextMockedStatic
                    .when(() -> ThreadContext.put(REQUEST_ID_HEADER, REQUEST_ID_HEADER_PREFIX + requestId))
                    .thenAnswer(invocation -> null);

            loggingFilter.doFilter(servletRequestMock, servletResponseMock, filterChain);

            uuidMockedStatic.verify(UUID::randomUUID);
            threadContextMockedStatic
                    .verify(() -> ThreadContext.put(REQUEST_ID_HEADER, REQUEST_ID_HEADER_PREFIX + requestId));
            verify(filterChain).doFilter(servletRequestMock, servletResponseMock);
            headerContextHolderMockedStatic.verify(HeaderContextHolder::isPresent);
            headerContextHolderMockedStatic.verify(() -> HeaderContextHolder.set(headers));
            headerContextHolderMockedStatic.verify(HeaderContextHolder::get);
            headerContextHolderMockedStatic.verify(HeaderContextHolder::remove);
            threadContextMockedStatic.verify(ThreadContext::clearMap);
        }
    }

    @Test
    void testDoFilterInternalWhenHeaderContextHolderIsPresent() throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        Map<String, String> headers = Map.of(REQUEST_ID_HEADER, requestId);

        try (MockedStatic<ThreadContext> threadContextMockedStatic = mockStatic(ThreadContext.class);
                MockedStatic<HeaderContextHolder> headerContextHolderMockedStatic = mockStatic(
                        HeaderContextHolder.class)) {

            headerContextHolderMockedStatic.when(HeaderContextHolder::isPresent).thenReturn(true);
            headerContextHolderMockedStatic.when(HeaderContextHolder::get).thenReturn(headers);

            loggingFilter.doFilter(servletRequestMock, servletResponseMock, filterChain);

            threadContextMockedStatic.verify(() -> ThreadContext.put(REQUEST_ID_HEADER, requestId));
            verify(filterChain).doFilter(servletRequestMock, servletResponseMock);
            headerContextHolderMockedStatic.verify(HeaderContextHolder::isPresent);
            headerContextHolderMockedStatic.verify(() -> HeaderContextHolder.set(headers), never());
            headerContextHolderMockedStatic.verify(HeaderContextHolder::get);
            headerContextHolderMockedStatic.verify(HeaderContextHolder::remove);
            threadContextMockedStatic.verify(ThreadContext::clearMap);
        }
    }
}
