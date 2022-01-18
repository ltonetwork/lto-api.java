package com.ltonetwork.client.types;

import com.google.common.primitives.Bytes;

public class PublicKey extends Key {

    public PublicKey(byte[] valueBytes, Encoding encoding) {
        super(valueBytes, encoding);
    }

    public PublicKey(byte[] valueBytes, Encoding encoding, KeyType keyType) {
        super(valueBytes, encoding, keyType);
    }

    public PublicKey(String valueBytes, Encoding encoding) {
        super(valueBytes, encoding);
    }

    public PublicKey(String valueBytes, Encoding encoding, KeyType keyType) {
        super(valueBytes, encoding, keyType);
    }

    public PublicKey(byte[] valueBytes) {
        super(valueBytes);
    }

    public PublicKey(byte[] valueBytes, KeyType keyType) {
        super(valueBytes, keyType);
    }

    public PublicKey(String valueBytes) {
        super(valueBytes);
    }

    public PublicKey(String valueBytes, KeyType keyType) {
        super(valueBytes, keyType);
    }

    public byte[] toBinary() {
        switch(this.getKeyType()) {
            case ED25519:
                return Bytes.concat(new byte[]{0}, this.toRaw());
            case SECP256K1:
                return Bytes.concat(new byte[]{1}, this.toRaw());
            case SECP256R1:
                return Bytes.concat(new byte[]{2}, this.toRaw());
            default: throw new IllegalArgumentException("Unknown key type");
        }
    }
}
