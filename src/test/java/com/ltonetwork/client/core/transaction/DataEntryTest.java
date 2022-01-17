package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.core.transacton.DataEntry;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DataEntryTest {
    @Test
    public void testGetType() {
        DataEntry<Integer> entry = new DataEntry<>(DataEntry.DataEntryType.INTEGER, "entry1", 1);
        assertEquals(DataEntry.DataEntryType.INTEGER, entry.getType());
    }

    @Test
    public void testGetKey() {
        DataEntry<Integer> entry = new DataEntry<>(DataEntry.DataEntryType.INTEGER, "entry1", 1);
        assertEquals("entry1", entry.getKey());
    }

    @Test
    public void testGetValue() {
        DataEntry<Integer> entry = new DataEntry<>(DataEntry.DataEntryType.INTEGER, "entry1", 1);
        assertEquals(1, entry.getValue().intValue());
    }

    @Test
    public void testToBytesInteger() {
        DataEntry<Integer> entry = new DataEntry<>(DataEntry.DataEntryType.INTEGER, "test", 1);
        // 0, 4 - key length
        // 116, 101, 115, 116 - key ("test")
        // 0 - value type (int)
        // 0, 0, 0, 1 - integer (1)
        assertArrayEquals(new byte[]{0, 4, 116, 101, 115, 116, 0, 0, 0, 0, 1}, entry.toBytes());
    }

    @Test
    public void testToBytesBoolean() {
        DataEntry<Boolean> entry = new DataEntry<>(DataEntry.DataEntryType.BOOLEAN, "test", true);
        // 0, 4 - key length
        // 116, 101, 115, 116 - key ("test")
        // 1 - value type (boolean)
        // 1 - boolean (true)
        assertArrayEquals(new byte[]{0, 4, 116, 101, 115, 116, 1, 1}, entry.toBytes());
    }

    @Test
    public void testToBytesBinary() {
        DataEntry<byte[]> entry = new DataEntry<>(DataEntry.DataEntryType.BINARY, "test", new byte[]{1, 5, 10});
        // 0, 4 - key length
        // 116, 101, 115, 116 - key ("test")
        // 2 - value type (binary)
        // 0, 3 - binary length
        // 1, 5, 10 - binary
        assertArrayEquals(new byte[]{0, 4, 116, 101, 115, 116, 2, 0, 3, 1, 5, 10}, entry.toBytes());
    }

    @Test
    public void testToBytesString() {
        DataEntry<String> entry = new DataEntry<>(DataEntry.DataEntryType.STRING, "test", "test");
        // 0, 4 - key length
        // 116, 101, 115, 116 - key ("test")
        // 3 - value type (string)
        // 0, 4 - string length
        // 116, 101, 115, 116 - string ("test")
        assertArrayEquals(new byte[]{0, 4, 116, 101, 115, 116, 3, 0, 4, 116, 101, 115, 116}, entry.toBytes());
    }
}
