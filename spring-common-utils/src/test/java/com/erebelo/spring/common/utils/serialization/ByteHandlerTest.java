package com.erebelo.spring.common.utils.serialization;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.erebelo.spring.common.utils.serialization.model.ByteWrapperObject;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class ByteHandlerTest {

    @Test
    void testByteGeneratorWithValidObject() {
        TestObject testObject = new TestObject("Test");
        ByteWrapperObject response = ByteHandler.byteGenerator(testObject);

        assertNotNull(response);
        assertTrue(response.getByteArray().length > 0);
    }

    @Test
    void testByteGeneratorWithNullObject() {
        ByteWrapperObject response = ByteHandler.byteGenerator(null);

        assertNotNull(response);
        assertTrue(response.getByteArray().length > 0);
    }

    @Test
    void testByteGeneratorWithNonSerializableObject() {
        Object nonSerializableObject = new Object();
        ByteWrapperObject response = ByteHandler.byteGenerator(nonSerializableObject);

        assertNull(response);
    }

    @Test
    void testByteArrayComparisonWithMatchingArrays() {
        TestObject obj1 = new TestObject("Test");
        TestObject obj2 = new TestObject("Test");

        ByteWrapperObject bwo1 = ByteHandler.byteGenerator(obj1);
        ByteWrapperObject bwo2 = ByteHandler.byteGenerator(obj2);
        boolean response = ByteHandler.byteArrayComparison(bwo1, bwo2);

        assertTrue(response);
    }

    @Test
    void testByteArrayComparisonWithNonMatchingArrays() {
        TestObject obj1 = new TestObject("Test1");
        TestObject obj2 = new TestObject("Test2");

        ByteWrapperObject bwo1 = ByteHandler.byteGenerator(obj1);
        ByteWrapperObject bwo2 = ByteHandler.byteGenerator(obj2);
        boolean response = ByteHandler.byteArrayComparison(bwo1, bwo2);

        assertFalse(response);
    }

    @Test
    void testByteArrayComparisonWithNullOldObject() {
        TestObject obj1 = new TestObject("Test");

        ByteWrapperObject bwo1 = ByteHandler.byteGenerator(obj1);
        boolean response = ByteHandler.byteArrayComparison(null, bwo1);

        assertFalse(response);
    }

    @Test
    void testByteArrayComparisonWithNullNewObject() {
        TestObject obj1 = new TestObject("Test");

        ByteWrapperObject bwo1 = ByteHandler.byteGenerator(obj1);
        boolean response = ByteHandler.byteArrayComparison(bwo1, null);

        assertFalse(response);
    }

    @Test
    void testByteArrayComparisonWithBothNullObjects() {
        boolean response = ByteHandler.byteArrayComparison(null, null);

        assertFalse(response);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class TestObject implements Serializable {
        private String name;
    }
}
