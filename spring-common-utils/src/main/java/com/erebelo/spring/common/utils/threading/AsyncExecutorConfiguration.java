package com.erebelo.spring.common.utils.threading;

import com.erebelo.spring.common.utils.http.HeaderContextHolder;
import com.erebelo.spring.common.utils.http.HttpTraceHeader;
import java.util.Map;
import java.util.concurrent.Executor;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Configuration class for setting up an asynchronous task executor with a
 * customized thread pool.
 * <p>
 * This class is conditionally enabled based on the property:
 * 
 * <pre>
 * spring.common.async-task-executor.enabled=true
 * </pre>
 * 
 * If the property is not explicitly set, it defaults to **disabled**.
 * </p>
 * <p>
 * This class is used when injecting an {@link Executor} or
 * {@link ThreadPoolTaskExecutor} dependency into a service class. It is only
 * necessary to configure this if there is a need to modify the thread pool
 * settings, such as core pool size, maximum pool size, or queue capacity. If
 * the default thread pool configuration is sufficient, and only the propagation
 * of HTTP request attributes and {@link ThreadContext} to new threads is
 * required, then using {@link AsyncThreadContext} alone is enough without the
 * need for custom thread pool configurations.
 * </p>
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.common", name = "async-task-executor.enabled", havingValue = "true")
public class AsyncExecutorConfiguration {

    /**
     * Creates and configures a ThreadPoolTaskExecutor for executing asynchronous
     * tasks.
     *
     * @return a configured ThreadPoolTaskExecutor instance
     */
    @Bean
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.setTaskDecorator(new ContextCopyingTaskDecorator());
        executor.initialize();
        return executor;
    }

    /**
     * A TaskDecorator implementation that copies the current request attributes and
     * HTTP headers to the runnable task's execution context. It ensures that
     * request-scoped data, such as HTTP headers, and logging context are preserved
     * when the task is executed in a different thread.
     * <p>
     * This decorator uses:
     * </p>
     * <ul>
     * <li>HeaderContextHolder to handle HTTP headers (for request-scoped data)</li>
     * <li>ThreadContext to manage the logging context (for logging scope)</li>
     * </ul>
     */
    static class ContextCopyingTaskDecorator implements TaskDecorator {
        @Override
        public @NonNull Runnable decorate(@NonNull Runnable runnable) {
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
    }
}
