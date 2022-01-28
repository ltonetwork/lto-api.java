package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.JsonUtil;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DataEntry<T extends Serializable> {
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

    public byte[] toBinary() {
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

    public JsonObject toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        switch (type) {
            case INTEGER:
                sb.append("'type': int").append(", ");
                sb.append("'key': ").append(key).append(", ");
                sb.append("'value': ").append(value).append(", ");
                break;
            case BINARY:
                byte[] serialized = SerializationUtils.serialize(value);
                sb.append("'type': binary").append(", ");
                sb.append("'key': ").append(key).append(", ");
                sb.append("'value': ").append(Arrays.toString(serialized)).append(", ");
                break;
            case BOOLEAN:
                sb.append("'type': boolean").append(", ");
                sb.append("'key': ").append(key).append(", ");
                sb.append("'value': ").append(value).append(", ");
                break;
            case STRING:
                sb.append("'type': string").append(", ");
                sb.append("'key': ").append(key).append(", ");
                sb.append("'value': ").append(value).append(", ");
                break;
        }
        sb.append("}");

        return JsonUtil.jsonDecode(sb.toString());
    }

    public enum DataEntryType {
        // N.B.: Integer meant as number, not as integer type, actual type is Long
        INTEGER,
        BOOLEAN,
        BINARY,
        STRING
    }
}
