package com.ltonetwork.client.core;

import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.utils.Encoder;

public class KeyPair {
    private Key secretkey;
    private Key publickey;

    public KeyPair(Key publickey, Key secretkey) {
        if (publickey != null) {
            this.publickey = publickey;
        }
        if (secretkey != null) {
            this.secretkey = secretkey;
        }
    }

    public KeyPair(byte[] publickey, byte[] secretkey, Encoding encoding) {
        if (publickey != null) {
            this.publickey = new Key(
                    publickey.clone(),
                    encoding
            );
        }
        if (secretkey != null) {
            this.secretkey = new Key(
                    secretkey.clone(),
                    encoding
            );
        }
    }

    public KeyPair() {
        this.publickey = null;
        this.secretkey = null;
    }

    public void setSecretkey(Key secretkey) {
        this.secretkey = secretkey;
    }

    public void setSecretkey(byte[] secretkey, Encoding encoding) {
        this.secretkey = new Key(
                secretkey.clone(),
                encoding
        );
    }

    public Key getSecretkey() {
        return this.secretkey;
    }

    public void setPublickey(Key publickey) {
        this.publickey = publickey;
    }

    public void setPublickey(byte[] publickey, Encoding encoding) {
        this.publickey = new Key(
                publickey.clone(),
                encoding
        );
    }

    public Key getPublickey() {
        return this.publickey;
    }
}
