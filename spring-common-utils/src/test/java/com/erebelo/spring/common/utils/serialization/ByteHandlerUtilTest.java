package com.erebelo.spring.common.utils.serialization;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class ByteHandlerUtilTest {

    @Test
    void testByteGeneratorWithValidObject() {
        TestObject testObject = new TestObject("Test");
        var result = ByteHandlerUtil.byteGenerator(testObject);

        assertNotNull(result);
        assertTrue(result.getByteArray().length > 0);
    }

    @Test
    void testByteGeneratorWithNullObject() {
        var result = ByteHandlerUtil.byteGenerator(null);

        assertNotNull(result);
        assertTrue(result.getByteArray().length > 0);
    }

    @Test
    void testByteGeneratorWithNonSerializableObject() {
        Object nonSerializableObject = new Object();
        var result = ByteHandlerUtil.byteGenerator(nonSerializableObject);

        assertNull(result);
    }

    @Test
    void testByteArrayComparisonWithMatchingArrays() {
        TestObject obj1 = new TestObject("Test");
        TestObject obj2 = new TestObject("Test");

        var bwo1 = ByteHandlerUtil.byteGenerator(obj1);
        var bwo2 = ByteHandlerUtil.byteGenerator(obj2);
        boolean result = ByteHandlerUtil.byteArrayComparison(bwo1, bwo2);

        assertTrue(result);
    }

    @Test
    void testByteArrayComparisonWithNonMatchingArrays() {
        TestObject obj1 = new TestObject("Test1");
        TestObject obj2 = new TestObject("Test2");

        var bwo1 = ByteHandlerUtil.byteGenerator(obj1);
        var bwo2 = ByteHandlerUtil.byteGenerator(obj2);
        boolean result = ByteHandlerUtil.byteArrayComparison(bwo1, bwo2);

        assertFalse(result);
    }

    @Test
    void testByteArrayComparisonWithNullOldObject() {
        TestObject obj1 = new TestObject("Test");

        var bwo1 = ByteHandlerUtil.byteGenerator(obj1);
        boolean result = ByteHandlerUtil.byteArrayComparison(null, bwo1);

        assertFalse(result);
    }

    @Test
    void testByteArrayComparisonWithNullNewObject() {
        TestObject obj1 = new TestObject("Test");

        var bwo1 = ByteHandlerUtil.byteGenerator(obj1);
        boolean result = ByteHandlerUtil.byteArrayComparison(bwo1, null);

        assertFalse(result);
    }

    @Test
    void testByteArrayComparisonWithBothNullObjects() {
        boolean result = ByteHandlerUtil.byteArrayComparison(null, null);

        assertFalse(result);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class TestObject implements Serializable {
        private String name;
    }
}
