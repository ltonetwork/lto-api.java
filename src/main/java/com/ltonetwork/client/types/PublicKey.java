package com.ltonetwork.client.types;

import com.google.common.primitives.Bytes;
import com.ltonetwork.seasalt.Binary;

public class PublicKey extends Key {

    public PublicKey(byte[] valueBytes, Key.KeyType keyType) {
        super(valueBytes, keyType);
    }

    public PublicKey(byte[] valueBytes) {
        super(valueBytes);
    }

    public PublicKey(String value, Encoding encoding, Key.KeyType keyType) {
        super(value, encoding, keyType);
    }

    public PublicKey(String value, Encoding encoding) {
        super(value, encoding);
    }

    public PublicKey(Binary value, Key.KeyType keyType) {
        super(value, keyType);
    }

    public PublicKey(Binary value) {
        super(value);
    }

    public byte[] toBinary() {
        switch (this.getType()) {
            case ED25519:
                return Bytes.concat(new byte[]{1}, this.getRaw());
            case SECP256K1:
                return Bytes.concat(new byte[]{2}, this.getRaw());
            case SECP256R1:
                return Bytes.concat(new byte[]{3}, this.getRaw());
            case CURVE25519:
                throw new IllegalArgumentException("Cannot convert encryption key of type CURVE25519 to binary");
            default:
                throw new IllegalArgumentException("Unknown key type");
        }
    }

    public short keyLengthByType(KeyType type) {
        switch (type) {
            case ED25519:
            case CURVE25519:
                return (short) 32;
            case SECP256K1:
            case SECP256R1:
                return (short) 33;
            default:
                throw new IllegalArgumentException("Unknown key type");
        }
    }

    public short keyLengthByType(byte id) {
        switch (id) {
            case 0:
                return keyLengthByType(KeyType.ED25519);
            case 1:
                return keyLengthByType(KeyType.SECP256K1);
            case 2:
                return keyLengthByType(KeyType.SECP256R1);
            default:
                throw new IllegalArgumentException("Unknown key type");
        }
    }
}
