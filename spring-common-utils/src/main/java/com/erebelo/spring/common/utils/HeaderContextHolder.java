package com.erebelo.spring.common.utils;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Wrapper class for managing HTTP header context using ThreadLocal storage.
 * Provides methods to set, get, and check the presence of HTTP headers for the
 * current thread.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderContextHolder {

    private static final ThreadLocal<Map<String, String>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    public static void set(Map<String, String> map) {
        threadLocal.set(map);
    }

    public static Map<String, String> get() {
        return threadLocal.get();
    }

    public static boolean isPresent() {
        return !threadLocal.get().isEmpty();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
