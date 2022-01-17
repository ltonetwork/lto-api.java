package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;

public class DataEntry<T> {
    private final DataEntryType type;
    private final String key;
    private final T value;

    public DataEntry(DataEntryType type, String key, T value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public enum DataEntryType {
        INTEGER,
        BOOLEAN,
        BINARY,
        STRING
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

    public byte[] toBinary() {
        System.out.println(type);
        switch(type){
            case INTEGER:
                return Bytes.concat(new byte[]{0}, Ints.toByteArray((Integer) value));
            case BOOLEAN:
                return Bytes.concat(new byte[]{1}, (boolean) value ? new byte[]{1} : new byte[]{0});
            case BINARY:
                return Bytes.concat(new byte[]{2}, (byte[]) value);
            case STRING:
                return Bytes.concat(new byte[]{3}, value.toString().getBytes());
            default: throw new IllegalArgumentException("Unknown DataEntry type");
        }
    }
}
