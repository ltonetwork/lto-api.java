package com.ltonetwork.client.types;

import com.ltonetwork.seasalt.Binary;

public class KeyPair {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public KeyPair(Binary publicKey, Binary privateKey, Key.KeyType keyType) {
        this.publicKey = new PublicKey(publicKey, keyType);
        this.privateKey = new PrivateKey(privateKey, keyType);
    }

    public KeyPair(Binary publicKey, Binary privateKey) {
        this(publicKey, privateKey, Key.KeyType.ED25519);
    }

    public KeyPair(byte[] publicKey, byte[] privateKey, Key.KeyType keyType) {
        this.publicKey = new PublicKey(publicKey.clone(), keyType);
        this.privateKey = new PrivateKey(privateKey.clone(), keyType);
    }

    public KeyPair(byte[] publicKey, byte[] privateKey) {
        this(publicKey, privateKey, Key.KeyType.ED25519);
    }

    public KeyPair(String publicKey, String privateKey, Encoding encoding) {
        this.publicKey = new PublicKey(publicKey, encoding);
        this.privateKey = new PrivateKey(privateKey, encoding);
    }

    public KeyPair(String publicKey, String privateKey) {
        this(publicKey, privateKey, Encoding.BASE58);
    }

    public KeyPair(com.ltonetwork.seasalt.KeyPair keyPair) {
        this(keyPair.getPublicKey(), keyPair.getPrivateKey());
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }
}
