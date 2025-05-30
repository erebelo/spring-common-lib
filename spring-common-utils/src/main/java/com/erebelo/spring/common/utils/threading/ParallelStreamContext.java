package com.erebelo.spring.common.utils.threading;

import com.erebelo.spring.common.utils.http.HeaderContextHolder;
import com.erebelo.spring.common.utils.http.HttpTraceHeader;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@UtilityClass
public class ParallelStreamContext {

    /**
     * Processes each element of the given stream in parallel while preserving the
     * request and logging contexts across threads. The contexts are set before
     * executing the action and cleaned up afterward.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
     * ParallelStreamContext.forEach(arrayObject.stream(), obj -> {
     * 	// Code that needs access to the request attributes and ThreadContext
     * });
     * </pre>
     *
     * @param stream
     *            the stream of elements to process
     * @param action
     *            the action to perform on each element
     * @param <T>
     *            the type of elements in the stream
     */
    public static <T> void forEach(Stream<T> stream, Consumer<T> action) {
        long mainThreadId = Thread.currentThread().getId();
        RequestAttributes contextAttributes = HttpTraceHeader.getRequestAttributes();
        Map<String, String> loggingContext = HeaderContextHolder.get();

        stream.parallel().forEach(item -> {
            // Set the current request attributes for the new thread
            RequestContextHolder.setRequestAttributes(contextAttributes);

            // Set the current HTTP headers for the new thread using HeaderContextHolder and
            // ThreadContext
            if (!HeaderContextHolder.isPresent() && ThreadContext.isEmpty() && loggingContext != null) {
                HeaderContextHolder.set(loggingContext);
                ThreadContext.putAll(loggingContext);
            }

            try {
                action.accept(item);
            } finally {
                if (Thread.currentThread().getId() != mainThreadId) {
                    RequestContextHolder.resetRequestAttributes();
                    HeaderContextHolder.remove();
                    ThreadContext.clearAll();
                }
            }
        });
    }
}
