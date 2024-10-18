package com.erebelo.spring.common.utils.threading;

import static com.erebelo.spring.common.utils.http.HttpTraceHeader.getDefaultHttpTraceHeaders;
import static com.erebelo.spring.common.utils.http.HttpTraceHeader.getHttpServletRequest;

import java.util.Map;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.ThreadContext;

@UtilityClass
public class AsyncThreadContext {

    /**
     * Wraps a Runnable to carry over the current ThreadContext to a new thread
     * created by CompletableFuture's runAsync() method. This keeps the
     * ThreadContext data accessible in the new thread.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
     * CompletableFuture.runAsync(withThreadContext(() -> {
     * 	// Code that needs the ThreadContext
     * }));
     * </pre>
     *
     * @param runnable
     *            the Runnable to execute in the new thread
     * @return a Runnable that restores the ThreadContext and runs the provided task
     */
    public static Runnable withThreadContext(Runnable runnable) {
        Map<String, String> httpHeaders = getDefaultHttpTraceHeaders(getHttpServletRequest());
        return () -> {
            ThreadContext.putAll(httpHeaders);
            runnable.run();
        };
    }

    /**
     * Wraps a Supplier to carry over the current ThreadContext to a new thread
     * created by CompletableFuture's supplyAsync() method. This ensures that the
     * ThreadContext data is accessible in the new thread.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
     * CompletableFuture.supplyAsync(withThreadContext(() -> {
     * 	// Code that needs the ThreadContext
     * 	return someValue;
     * }));
     * </pre>
     *
     * @param supplier
     *            the Supplier to execute in the new thread
     * @return a Supplier that restores the ThreadContext and returns the value from
     *         the original supplier
     */
    public static <U> Supplier<U> withThreadContext(Supplier<U> supplier) {
        Map<String, String> httpHeaders = getDefaultHttpTraceHeaders(getHttpServletRequest());
        return () -> {
            ThreadContext.putAll(httpHeaders);
            return supplier.get();
        };
    }
}
