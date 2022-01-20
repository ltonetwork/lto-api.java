package com.ltonetwork.client.core;

import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.KeyPair;
import com.ltonetwork.client.types.PublicKey;
import com.ltonetwork.client.utils.CryptoUtil;
import com.ltonetwork.seasalt.sign.Signature;

import java.nio.charset.StandardCharsets;

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

    public PublicKey getPublicSignKey() {
        return sign.getPublicKey();
    }

    public PublicKey getPublicEncryptKey() {
        return encrypt.getPublicKey();
    }

    public Signature sign(byte[] message) {
        if (sign == null || sign.getPrivateKey() == null) {
            throw new RuntimeException("Unable to sign message; no secret sign key");
        }

        return CryptoUtil.signDetached(message, sign.getPrivateKey());
    }
    public Signature sign(String message) {
        return sign(message.getBytes(StandardCharsets.UTF_8));
    }

    public boolean verify(Signature signature, byte[] message) {
        if (sign == null || sign.getPublicKey() == null) {
            throw new RuntimeException("Unable to verify message; no public sign key");
        }

        return CryptoUtil.verify(signature, message, sign.getPublicKey());
    }
    public boolean verify(Signature signature, String message) {
        return verify(signature, message.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] encrypt(Account recipient, String message) {
        if (encrypt == null || encrypt.getPrivateKey() == null) {
            throw new RuntimeException("Unable to encrypt message; no secret encryption key");
        }
        if (recipient.encrypt == null || recipient.encrypt.getPublicKey() == null) {
            throw new RuntimeException("Unable to encrypt message; no public encryption key for recipient");
        }

        byte[] nonce = getNonce();

        byte[] retEncrypt = CryptoUtil.cryptoBox(
                nonce,
                message.getBytes(),
                recipient.encrypt.getPublicKey().getRaw(),
                encrypt.getPrivateKey().getRaw()
        );

        byte[] ret = new byte[retEncrypt.length + nonce.length];
        System.arraycopy(retEncrypt, 0, ret, 0, retEncrypt.length);
        System.arraycopy(nonce, 0, ret, retEncrypt.length, nonce.length);

        return ret;
    }

    public byte[] decrypt(Account sender, byte[] ciphertext) {
        if (encrypt == null || encrypt.getPrivateKey() == null) {
            throw new RuntimeException("Unable to decrypt message; no secret encryption key");
        }
        if (sender.encrypt == null || sender.encrypt.getPublicKey() == null) {
            throw new RuntimeException("Unable to decrypt message; no public encryption key for recipient");
        }

        byte[] encryptedMessage = new byte[ciphertext.length - 24];
        System.arraycopy(ciphertext, 0, encryptedMessage, 0, ciphertext.length - 24);

        byte[] nonce = new byte[24];
        System.arraycopy(ciphertext, ciphertext.length - 24, nonce, 0, 24);

        return CryptoUtil.cryptoBoxOpen(
                nonce,
                encryptedMessage,
                encrypt.getPublicKey().getRaw(),
                sender.encrypt.getPrivateKey().getRaw()
        );
    }

    protected byte[] getNonce() {
        return CryptoUtil.randomBytes(CryptoUtil.cryptoBoxNoncebytes());
    }
}