package com.ltonetwork.client.core;

import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.types.*;
import com.ltonetwork.client.utils.CryptoUtil;

public class Account {

    private final Address address;
    private final KeyPair encrypt;
    private final KeyPair sign;

    public Account(Address address, KeyPair encrypt, KeyPair sign) {
        this.address = address;
        this.encrypt = encrypt;
        this.sign = sign;
    }

    public Address getAddressStruct() {
        return this.address;
    }

    public String getAddress(Encoding encoding) {
        String ret;

        switch (encoding) {
            case BASE58:
                ret = this.address.getAddress();
                break;
            case BASE64:
                ret = this.address.getAddressBase64();
                break;
            default:
                throw new InvalidArgumentException("Address is field supports only base58 and base64 encodings");
        }

        return ret;
    }

    public String getAddress() {
        return getAddress(Encoding.BASE58);
    }

    public byte getChainId() {
        return this.address.getChainId();
    }

    public KeyPair getEncrypt() {
        return encrypt;
    }

    public KeyPair getSign() {
        return sign;
    }

    public Key getPublicSignKey() {
        return sign.getPublickey();
    }

    public Key getPublicEncryptKey() {
        return encrypt.getPublickey();
    }

    public Signature sign(String message) {
        if (sign == null || sign.getSecretkey() == null) {
            throw new RuntimeException("Unable to sign message; no secret sign key");
        }
        byte[] signature = CryptoUtil.crypto_sign_detached(message.getBytes(), sign.getSecretkey().getValueBytes());
        return new Signature(signature);
    }

    public Signature sign(byte[] message) {
        if (sign == null || sign.getSecretkey() == null) {
            throw new RuntimeException("Unable to sign message; no secret sign key");
        }
        byte[] signature = CryptoUtil.crypto_sign_detached(message, sign.getSecretkey().getValueBytes());
        return new Signature(signature);
    }

    public boolean verify(Signature signature, String message) {
        if (sign == null || sign.getPublickey() == null) {
            throw new RuntimeException("Unable to verify message; no public sign key");
        }

        return signature.verify(sign.getPublickey(), message);
    }

    public boolean verify(Signature signature, byte[] message) {
        if (sign == null || sign.getPublickey() == null) {
            throw new RuntimeException("Unable to verify message; no public sign key");
        }

        return signature.verify(sign.getPublickey(), message);
    }

    public byte[] encrypt(Account recipient, String message) {
        if (encrypt == null || encrypt.getSecretkey() == null) {
            throw new RuntimeException("Unable to encrypt message; no secret encryption key");
        }
        if (recipient.encrypt == null || recipient.encrypt.getPublickey() == null) {
            throw new RuntimeException("Unable to encrypt message; no public encryption key for recipient");
        }

        byte[] nonce = getNonce();

        byte[] retEncrypt = CryptoUtil.crypto_box(
                nonce,
                message.getBytes(),
                recipient.encrypt.getPublickey().getValueBytes(),
                encrypt.getSecretkey().getValueBytes()
        );

        byte[] ret = new byte[retEncrypt.length + nonce.length];
        System.arraycopy(retEncrypt, 0, ret, 0, retEncrypt.length);
        System.arraycopy(nonce, 0, ret, retEncrypt.length, nonce.length);

        return ret;
    }

    public byte[] decrypt(Account sender, byte[] ciphertext) {
        if (encrypt == null || encrypt.getSecretkey() == null) {
            throw new RuntimeException("Unable to decrypt message; no secret encryption key");
        }
        if (sender.encrypt == null || sender.encrypt.getPublickey() == null) {
            throw new RuntimeException("Unable to decrypt message; no public encryption key for recipient");
        }

        byte[] encryptedMessage = new byte[ciphertext.length - 24];
        System.arraycopy(ciphertext, 0, encryptedMessage, 0, ciphertext.length - 24);

        byte[] nonce = new byte[24];
        System.arraycopy(ciphertext, ciphertext.length - 24, nonce, 0, 24);

        return CryptoUtil.crypto_box_open(
                nonce,
                encryptedMessage,
                encrypt.getPublickey().getValueBytes(),
                sender.encrypt.getSecretkey().getValueBytes()
        );
    }

    protected byte[] getNonce() {
        return CryptoUtil.random_bytes(CryptoUtil.crypto_box_noncebytes());
    }
}