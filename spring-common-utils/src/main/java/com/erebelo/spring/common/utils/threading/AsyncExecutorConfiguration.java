package com.erebelo.spring.common.utils.threading;

import com.erebelo.spring.common.utils.http.HeaderContextHolder;
import com.erebelo.spring.common.utils.http.HttpTraceHeader;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Configuration
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
     * HTTP headers to the runnable task's execution context.
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

                    // Set the current http headers for the new thread
                    HeaderContextHolder.set(httpHeaders);

                    runnable.run();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                    HeaderContextHolder.remove();
                }
            };
        }
    }
}
