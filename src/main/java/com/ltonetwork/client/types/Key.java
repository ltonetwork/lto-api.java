package com.ltonetwork.client.types;

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

    public Key(byte[] valueBytes) {
        this.valueBytes = valueBytes;
        this.encoding = Encoding.BASE58;
    }

    public Key(String valueBytes) {
        this.valueBytes = valueBytes.getBytes(StandardCharsets.UTF_8);
        this.encoding = Encoding.BASE58;
    }

    public byte[] getValueBytes() {
        return valueBytes;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public String toBase58() {
        String ret;

        switch (this.encoding) {
            case BASE58:
                ret = new String(this.valueBytes);
                break;
            case BASE64:
                ret = base58Encode(base64Decode(this.valueBytes));
                break;
            case RAW:
                ret = base58Encode(this.valueBytes);
                break;
            case HEX:
                ret = base58Encode(hexDecode(this.valueBytes));
                break;
            default:
                ret = null;
        }

        return ret;
    }

    public String toBase64() {
        String ret;

        switch (this.encoding) {
            case BASE58:
                ret = base64Encode(base58Decode(this.valueBytes));
                break;
            case BASE64:
                ret = new String(this.valueBytes);
                break;
            case RAW:
                ret = base64Encode(this.valueBytes);
                break;
            case HEX:
                ret = base64Encode(hexDecode(this.valueBytes));
                break;
            default:
                ret = null;
        }

        return ret;
    }

    public byte[] toRaw() {
        byte[] ret;

        switch (this.encoding) {
            case BASE58:
                ret = base58Decode(this.valueBytes);
                break;
            case BASE64:
                ret = base64Decode(this.valueBytes);
                break;
            case RAW:
                ret = new String(this.valueBytes).getBytes(StandardCharsets.UTF_8);
                break;
            case HEX:
                ret = hexDecode(this.valueBytes);
                break;
            default:
                ret = null;
        }

        return ret;
    }

    public String toHex() {
        String ret;

        switch (this.encoding) {
            case BASE58:
                ret = hexEncode(base58Decode(this.valueBytes));
                break;
            case BASE64:
                ret = hexEncode(base64Decode(this.valueBytes));
                break;
            case RAW:
                ret = hexEncode(this.valueBytes);
                break;
            case HEX:
                ret = new String(this.valueBytes);
                break;
            default:
                ret = null;
        }

        return ret;
    }
}
