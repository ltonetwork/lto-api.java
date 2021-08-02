package com.ltonetwork.client.types;

public class PublicKey extends Key {

    public PublicKey(byte[] valueBytes, Encoding encoding) {
        super(valueBytes, encoding);
    }

    public PublicKey(String valueBytes, Encoding encoding) {
        super(valueBytes, encoding);
    }

    public PublicKey(byte[] valueBytes) {
        super(valueBytes);
    }

    public PublicKey(String valueBytes) {
        super(valueBytes);
    }
}
