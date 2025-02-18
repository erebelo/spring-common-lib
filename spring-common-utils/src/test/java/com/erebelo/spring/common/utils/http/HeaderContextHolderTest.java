package com.erebelo.spring.common.utils.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class HeaderContextHolderTest {

    private static final Map<String, String> headers = Map.of("RequestID", "12345");

    @AfterEach
    void tearDown() {
        HeaderContextHolder.remove();
    }

    @Test
    void testSetAndGet() {
        HeaderContextHolder.set(headers);

        Map<String, String> response = HeaderContextHolder.get();

        assertEquals(headers, response);
    }

    @Test
    void testIsPresentWhenSet() {
        HeaderContextHolder.set(headers);

        assertTrue(HeaderContextHolder.isPresent());
    }

    @Test
    void testIsPresentWhenEmpty() {
        HeaderContextHolder.set(new HashMap<>());

        assertFalse(HeaderContextHolder.isPresent());
    }

    @Test
    void testRemove() {
        HeaderContextHolder.set(headers);

        HeaderContextHolder.remove();

        assertFalse(HeaderContextHolder.isPresent());
        assertTrue(HeaderContextHolder.get().isEmpty());
    }

    @Test
    void testSetOverwritesPreviousHeaders() {
        HeaderContextHolder.set(headers);

        Map<String, String> newHeaders = Map.of("NewRequestID", "12345");
        HeaderContextHolder.set(newHeaders);

        assertEquals(newHeaders, HeaderContextHolder.get());
        assertFalse(HeaderContextHolder.get().containsKey("RequestID"));
    }
}
