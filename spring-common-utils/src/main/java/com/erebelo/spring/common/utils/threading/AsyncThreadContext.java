package com.erebelo.spring.common.utils.threading;

import com.erebelo.spring.common.utils.http.HeaderContextHolder;
import com.erebelo.spring.common.utils.http.HttpTraceHeader;
import java.util.Map;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@UtilityClass
public class AsyncThreadContext {

    /**
     * Wraps a Runnable to propagate the current HTTP request attributes and
     * ThreadContext (for logging) to a new thread created by CompletableFuture's
     * runAsync() method. This ensures that request-scoped data, such as headers and
     * context attributes, remain accessible in the new thread.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
     * CompletableFuture.runAsync(withThreadContext(() -> {
     * 	// Code that needs access to the request attributes and ThreadContext
     * }));
     * </pre>
     *
     * @param runnable
     *            the task to be executed in the new thread
     * @return a Runnable that restores the request attributes and ThreadContext
     *         before execution
     */
    public static Runnable withThreadContext(Runnable runnable) {
        RequestAttributes contextAttributes = HttpTraceHeader.getRequestAttributes();
        Map<String, String> httpHeaders = HttpTraceHeader
                .getDefaultHttpTraceHeaders(HttpTraceHeader.getHttpServletRequest());

        return () -> {
            try {
                // Set the current request attributes for the new thread
                RequestContextHolder.setRequestAttributes(contextAttributes);

                // Set the current HTTP headers for the new thread using HeaderContextHolder
                HeaderContextHolder.set(httpHeaders);

                // Set the current HTTP headers for the new thread using ThreadContext
                ThreadContext.putAll(httpHeaders);

                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                HeaderContextHolder.remove();
                ThreadContext.clearAll();
            }
        };
    }

    /**
     * Wraps a Supplier to propagate the current HTTP request attributes and
     * ThreadContext (for logging) to a new thread created by CompletableFuture's
     * supplyAsync() method. This ensures that request-scoped data, such as headers
     * and context attributes, remain accessible in the new thread.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
     * CompletableFuture.supplyAsync(withThreadContext(() -> {
     * 	// Code that needs access to the request attributes and ThreadContext
     * 	return someValue;
     * }));
     * </pre>
     *
     * @param supplier
     *            the task to be executed in the new thread
     * @param <U>
     *            the return type of the Supplier
     * @return a Supplier that restores the request attributes and ThreadContext
     *         before execution and returns the result from the original supplier
     */
    public static <U> Supplier<U> withThreadContext(Supplier<U> supplier) {
        RequestAttributes contextAttributes = HttpTraceHeader.getRequestAttributes();
        Map<String, String> httpHeaders = HttpTraceHeader
                .getDefaultHttpTraceHeaders(HttpTraceHeader.getHttpServletRequest());

        return () -> {
            try {
                // Set the current request attributes for the new thread
                RequestContextHolder.setRequestAttributes(contextAttributes);

                // Set the current HTTP headers for the new thread using HeaderContextHolder
                HeaderContextHolder.set(httpHeaders);

                // Set the current HTTP headers for the new thread using ThreadContext
                ThreadContext.putAll(httpHeaders);

                return supplier.get();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                HeaderContextHolder.remove();
                ThreadContext.clearAll();
            }
        };
    }
}
