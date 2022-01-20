package com.ltonetwork.client.types;

import com.ltonetwork.seasalt.Binary;

public class PrivateKey extends Key {

    public PrivateKey(byte[] valueBytes, Key.KeyType keyType) {
        super(valueBytes, keyType);
    }

    public PrivateKey(byte[] valueBytes) {
        super(valueBytes);
    }

    public PrivateKey(String value, Encoding encoding, Key.KeyType keyType) {
        super(value, encoding, keyType);
    }

    public PrivateKey(String value, Encoding encoding) {
        super(value, encoding);
    }

    public PrivateKey(Binary value, Key.KeyType keyType) {
        super(value, keyType);
    }

    public PrivateKey(Binary value) {
        super(value);
    }
}
