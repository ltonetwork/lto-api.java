package com.ltonetwork.client.types;

import com.ltonetwork.seasalt.Binary;
import org.apache.commons.codec.DecoderException;

public abstract class Key {

    private final Binary value;
    private final KeyType type;

    public enum KeyType {
        ED25519,
        SECP256K1,
        SECP256R1,
        CURVE25519
    }

    public Key(byte[] valueBytes, KeyType keyType) {
        this.value = new Binary(valueBytes);
        this.type = keyType;
    }

    public Key(byte[] valueBytes) {
        this(valueBytes, KeyType.ED25519);
    }

    public Key(String value, Encoding encoding, KeyType keyType) {
        this.value = valueToBinary(value, encoding);
        this.type = keyType;
    }

    public Key(String value, Encoding encoding) {
        this(value, encoding, KeyType.ED25519);
    }

    public Key(Binary value, KeyType keyType) {
        this.value = value;
        this.type = keyType;
    }

    public Key(Binary value) {
        this(value, KeyType.ED25519);
    }

    public String getBase58() {
        return value.getBase58();
    }

    public String getBase64() {
        return value.getBase64();
    }

    public String getHex() {
        return value.getHex();
    }

    public byte[] getRaw() {
        return value.getBytes();
    }

    public KeyType getType() {
        return type;
    }

    private Binary valueToBinary(String value, Encoding encoding) {
        try {
            switch (encoding) {
                case RAW: return new Binary(value.getBytes());
                case BASE58: return Binary.fromBase58(value);
                case BASE64: return Binary.fromBase64(value);
                case HEX: return Binary.fromHex(value);
                default: throw new IllegalArgumentException("Unknown encoding");
            }
        } catch (DecoderException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
