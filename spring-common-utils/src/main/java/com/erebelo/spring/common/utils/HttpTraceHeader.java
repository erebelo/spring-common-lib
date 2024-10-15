package com.erebelo.spring.common.utils;

import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Contains common http related parameters and functions used across modules
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpTraceHeader {

    private static final List<String> REQUEST_HEADER_LIST;

    private static final String REQUEST_ID_HEADER = "RequestID";
    private static final String REQUEST_ID_HEADER_PREFIX = "GEN-";

    static {
        REQUEST_HEADER_LIST = Collections.singletonList(REQUEST_ID_HEADER);
    }

    /**
     * Filters default HTTP headers from a given HttpServletRequest by calling the
     * method getDefaultHttpTraceHeaders(). Converts default HTTP headers from Map
     * structure to MultiValueMap, allowing for multiple values per key.
     *
     * @param httpServletRequest
     *            the HttpServletRequest containing the headers
     * @return a MultiValueMap containing the filtered and converted HTTP headers
     */
    public static MultiValueMap<String, String> getMultiValueMapDefaultHttpTraceHeaders(
            HttpServletRequest httpServletRequest) {
        Map<String, String> httpHeaders = getDefaultHttpTraceHeaders(httpServletRequest);

        MultiValueMap<String, String> multiValueMapHttpHeaders = new LinkedMultiValueMap<>();
        httpHeaders.forEach(multiValueMapHttpHeaders::add);

        return multiValueMapHttpHeaders;
    }

    /**
     * Filters HTTP headers from a given HttpServletRequest and adds them to a Map
     * structure, utilizing ThreadLocal storage for managing header context.
     *
     * @param httpServletRequest
     *            the HttpServletRequest containing the headers
     * @return a Map containing the filtered HTTP headers
     */
    public static Map<String, String> getDefaultHttpTraceHeaders(HttpServletRequest httpServletRequest) {
        if (!HeaderContextHolder.isPresent()) {
            Map<String, String> httpHeaders = sanitizeHeader(httpServletRequest);

            // If RequestID is not present, generate a new UUID
            if (!httpHeaders.containsKey(REQUEST_ID_HEADER)) {
                httpHeaders.put(REQUEST_ID_HEADER, REQUEST_ID_HEADER_PREFIX + UUID.randomUUID());
            }

            HeaderContextHolder.set(httpHeaders);
        }

        return HeaderContextHolder.get();
    }

    /**
     * Filters default http headers.
     *
     * @param httpServletRequest
     *            the HttpServletRequest containing the headers
     * @return a Map containing the filtered HTTP headers
     */
    private static Map<String, String> sanitizeHeader(HttpServletRequest httpServletRequest) {
        return REQUEST_HEADER_LIST.stream().filter(headerName -> {
            String headerValue = httpServletRequest.getHeader(headerName);
            return headerValue != null && !headerValue.trim().isEmpty();
        }).collect(Collectors.toMap(headerName -> headerName,
                headerName -> trimToEmpty(split(httpServletRequest.getHeader(headerName), ',')[0])));
    }

    /**
     * Retrieves the current HttpServletRequest from the RequestContextHolder .
     *
     * @return the current HttpServletRequest object
     * @throws IllegalStateException
     *             if no request is currently active
     */
    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new IllegalStateException("No current request attributes");
        } else {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
    }
}
