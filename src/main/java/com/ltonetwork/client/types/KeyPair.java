package com.ltonetwork.client.types;

public class KeyPair {
    private PrivateKey secretkey;
    private PublicKey publickey;

    public KeyPair(PublicKey publickey, PrivateKey secretkey) {
        if (publickey != null) {
            this.publickey = publickey;
        }
        if (secretkey != null) {
            this.secretkey = secretkey;
        }
    }

    public KeyPair(byte[] publickey, byte[] secretkey, Encoding encoding) {
        if (publickey != null) {
            this.publickey = new PublicKey(
                    publickey.clone(),
                    encoding
            );
        }
        if (secretkey != null) {
            this.secretkey = new PrivateKey(
                    secretkey.clone(),
                    encoding
            );
        }
    }

    public KeyPair(byte[] publickey, byte[] secretkey) {
        this(publickey, secretkey, Encoding.BASE58);
    }

    public KeyPair(String publickey, String secretkey, Encoding encoding) {
        if (publickey != null) {
            this.publickey = new PublicKey(
                    publickey,
                    encoding
            );
        }
        if (secretkey != null) {
            this.secretkey = new PrivateKey(
                    secretkey,
                    encoding
            );
        }
    }

    public KeyPair(String publickey, String secretkey) {
        this(publickey, secretkey, Encoding.BASE58);
    }

    public KeyPair() {
        this.publickey = null;
        this.secretkey = null;
    }

    public void setSecretkey(PrivateKey secretkey) {
        this.secretkey = secretkey;
    }

    public void setSecretkey(byte[] secretkey, Encoding encoding) {
        this.secretkey = new PrivateKey(
                secretkey.clone(),
                encoding
        );
    }

    public PrivateKey getSecretkey() {
        return this.secretkey;
    }

    public void setPublickey(PublicKey publickey) {
        this.publickey = publickey;
    }

    public void setPublickey(byte[] publickey, Encoding encoding) {
        this.publickey = new PublicKey(
                publickey.clone(),
                encoding
        );
    }

    public PublicKey getPublickey() {
        return this.publickey;
    }
}
