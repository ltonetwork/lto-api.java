package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;

import java.nio.charset.StandardCharsets;

public class DataEntry<T> {
    private final DataEntryType type;
    private final String key;
    private final T value;

    public DataEntry(DataEntryType type, String key, T value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public DataEntryType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public byte[] toBytes() {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes;
        switch (type) {
            case INTEGER:
                valueBytes = Longs.toByteArray((Long) value);
                return Bytes.concat(
                        Shorts.toByteArray((short) keyBytes.length),
                        keyBytes,
                        new byte[]{0},
                        valueBytes);
            case BOOLEAN:
                valueBytes = (boolean) value ? new byte[]{1} : new byte[]{0};
                return Bytes.concat(
                        Shorts.toByteArray((short) keyBytes.length),
                        keyBytes,
                        new byte[]{1},
                        valueBytes);
            case BINARY:
                valueBytes = (byte[]) value;
                return Bytes.concat(
                        Shorts.toByteArray((short) keyBytes.length),
                        keyBytes,
                        new byte[]{2},
                        Shorts.toByteArray((short) valueBytes.length),
                        valueBytes);
            case STRING:
                valueBytes = value.toString().getBytes();
                return Bytes.concat(
                        Shorts.toByteArray((short) keyBytes.length),
                        keyBytes,
                        new byte[]{3},
                        Shorts.toByteArray((short) valueBytes.length),
                        valueBytes);
            default:
                throw new IllegalArgumentException("Unknown DataEntry type");
        }
    }

    public enum DataEntryType {
        // N.B.: Integer meant as number, not as integer type, actual type is Long
        INTEGER,
        BOOLEAN,
        BINARY,
        STRING
    }
}
