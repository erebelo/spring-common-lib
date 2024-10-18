package com.erebelo.spring.common.logging;

import static com.erebelo.spring.common.utils.http.HttpTraceHeader.getDefaultHttpTraceHeaders;

import com.erebelo.spring.common.utils.http.HeaderContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A filter that populates the ThreadContext with HTTP headers for tracing
 * requests, replacing hyphens with underscores in header names.
 * <p>
 * The filter is conditionally managed based on the application's properties:
 *
 * <pre>
 * spring.common.logging-context-filter.enabled=true or false
 * </pre>
 * <p>
 * If the property is not specified, the filter defaults to being enabled.
 */
@Component
@ConditionalOnProperty(prefix = "spring.common", name = "logging-context-filter.enabled", matchIfMissing = true)
public class ThreadContextFilter extends OncePerRequestFilter {

    /**
     * Processes the HTTP request, adding headers to the ThreadContext and passing
     * the request along the filter chain. Clears the ThreadContext afterward to
     * prevent leaks.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Map<String, String> httpHeaders = getDefaultHttpTraceHeaders(request);
            httpHeaders.forEach(ThreadContext::put);

            filterChain.doFilter(request, response);
        } finally {
            HeaderContextHolder.remove();
            ThreadContext.clearMap();
        }
    }
}
