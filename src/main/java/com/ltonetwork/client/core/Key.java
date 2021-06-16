package com.ltonetwork.client.core;

import java.nio.charset.StandardCharsets;

import static com.ltonetwork.client.utils.Encoder.*;

public class Key {

    private byte[] valueBytes;
    private Encoding encoding;

    public Key(byte[] valueBytes, Encoding encoding) {
        this.valueBytes = valueBytes;
        this.encoding = encoding;
    }

    public Key(String valueBytes, Encoding encoding) {
        this.valueBytes = valueBytes.getBytes(StandardCharsets.UTF_8);
        this.encoding = encoding;
    }

    public byte[] getValueBytes() {
        return valueBytes;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public String toBase58() {
        return switch (this.encoding) {
            case BASE58 ->
                new String(this.valueBytes);
            case BASE64 ->
                base58Encode(base64Decode(this.valueBytes));
            case RAW ->
                base58Encode(this.valueBytes);
            case HEX ->
                base58Encode(hexDecode(this.valueBytes));
        };
    }

    public String toBase64() {
        return switch (this.encoding) {
            case BASE58 ->
                base64Encode(base58Decode(this.valueBytes));
            case BASE64 ->
                new String(this.valueBytes);
            case RAW ->
                base64Encode(this.valueBytes);
            case HEX ->
                base64Encode(hexDecode(this.valueBytes));
        };
    }

    public byte[] toRaw() {
        return switch (this.encoding) {
            case BASE58 ->
                base58Decode(this.valueBytes);
            case BASE64 ->
                base64Decode(this.valueBytes);
            case RAW ->
                new String(this.valueBytes).getBytes(StandardCharsets.UTF_8);
            case HEX ->
                hexDecode(this.valueBytes);
        };
    }

    public String toHex() {
        return switch (this.encoding) {
            case BASE58 ->
                hexEncode(base58Decode(this.valueBytes));
            case BASE64 ->
                hexEncode(base64Decode(this.valueBytes));
            case RAW ->
                hexEncode(this.valueBytes);
            case HEX ->
                new String(this.valueBytes);
        };
    }
}
