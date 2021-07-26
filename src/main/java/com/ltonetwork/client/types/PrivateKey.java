package com.ltonetwork.client.types;

public class PrivateKey extends Key {

    public PrivateKey(byte[] valueBytes, Encoding encoding) {
        super(valueBytes, encoding);
    }

    public PrivateKey(String valueBytes, Encoding encoding) {
        super(valueBytes, encoding);
    }

    public PrivateKey(byte[] valueBytes) {
        super(valueBytes);
    }

    public PrivateKey(String valueBytes) {
        super(valueBytes);
    }
}
