package com.erebelo.spring.common.utils.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class HttpTraceHeaderTest {

    @Mock
    private HttpServletRequest servletRequestMock;

    private static final String REQUEST_ID_HEADER = "RequestID";
    private static final String REQUEST_ID_HEADER_PREFIX = "GEN-";

    @Test
    void testGetMultiValueMapDefaultHttpTraceHeadersWithRequestID() {
        String requestId = UUID.randomUUID().toString();
        given(servletRequestMock.getHeader(REQUEST_ID_HEADER)).willReturn(requestId);

        Map<String, String> headers = Map.of(REQUEST_ID_HEADER, requestId);

        try (MockedStatic<HeaderContextHolder> headerContextHolderMockedStatic = mockStatic(
                HeaderContextHolder.class)) {

            headerContextHolderMockedStatic.when(HeaderContextHolder::isPresent).thenReturn(false);
            headerContextHolderMockedStatic.when(() -> HeaderContextHolder.set(headers)).thenAnswer(invocation -> null);
            headerContextHolderMockedStatic.when(HeaderContextHolder::get).thenReturn(headers);

            MultiValueMap<String, String> result = HttpTraceHeader
                    .getMultiValueMapDefaultHttpTraceHeaders(servletRequestMock);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(requestId, result.getFirst(REQUEST_ID_HEADER));

            headerContextHolderMockedStatic.verify(HeaderContextHolder::isPresent);
            headerContextHolderMockedStatic.verify(() -> HeaderContextHolder.set(headers));
            headerContextHolderMockedStatic.verify(HeaderContextHolder::get);
        }
    }

    @Test
    void testGetMultiValueMapDefaultHttpTraceHeadersWithNoRequestID() {
        given(servletRequestMock.getHeader(REQUEST_ID_HEADER)).willReturn(null);

        UUID requestId = UUID.randomUUID();
        Map<String, String> headers = Map.of(REQUEST_ID_HEADER, REQUEST_ID_HEADER_PREFIX + requestId);

        try (MockedStatic<UUID> uuidMockedStatic = mockStatic(UUID.class);
                MockedStatic<HeaderContextHolder> headerContextHolderMockedStatic = mockStatic(
                        HeaderContextHolder.class)) {

            uuidMockedStatic.when(UUID::randomUUID).thenReturn(requestId);
            headerContextHolderMockedStatic.when(HeaderContextHolder::isPresent).thenReturn(false);
            headerContextHolderMockedStatic.when(() -> HeaderContextHolder.set(headers)).thenAnswer(invocation -> null);
            headerContextHolderMockedStatic.when(HeaderContextHolder::get).thenReturn(headers);

            MultiValueMap<String, String> result = HttpTraceHeader
                    .getMultiValueMapDefaultHttpTraceHeaders(servletRequestMock);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(Objects.requireNonNull(result.getFirst(REQUEST_ID_HEADER)).startsWith(REQUEST_ID_HEADER_PREFIX),
                    "Expected value to start with 'DD-'");

            uuidMockedStatic.verify(UUID::randomUUID);
            headerContextHolderMockedStatic.verify(HeaderContextHolder::isPresent);
            headerContextHolderMockedStatic.verify(() -> HeaderContextHolder.set(headers));
            headerContextHolderMockedStatic.verify(HeaderContextHolder::get);
        }
    }

    @Test
    void testGetHttpServletRequestWithRequestAttributes() {
        RequestAttributes mockRequestAttributes = new ServletRequestAttributes(servletRequestMock);
        RequestContextHolder.setRequestAttributes(mockRequestAttributes);

        HttpServletRequest result = HttpTraceHeader.getHttpServletRequest();

        assertNotNull(result);
        assertEquals(servletRequestMock, result);

        RequestContextHolder.setRequestAttributes(null);
    }

    @Test
    void testGetHttpServletRequestWithoutRequestAttributes() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                HttpTraceHeader::getHttpServletRequest);

        assertEquals("No current request attributes", exception.getMessage());
    }
}
