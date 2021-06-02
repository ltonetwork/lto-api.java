package legalthings.lto_api.lto.core;

import legalthings.lto_api.utils.main.CryptoUtil;
import legalthings.lto_api.utils.main.StringUtil;

public class Account {

    private byte[] address;
    private KeyPair encrypt;
    private KeyPair sign;

    public Account(byte[] address, KeyPair encrypt, KeyPair sign) {
        this.address = address;
        this.encrypt = encrypt;
        this.sign = sign;
    }

    public String getAddress(String encoding) {
        return address != null ? encode(address, encoding) : null;
    }

    public String getAddress() {
        return getAddress("base58");
    }

    public void setAddress(byte[] address) {
        this.address = address;
    }

    public KeyPair getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(KeyPair encrypt) {
        this.encrypt = encrypt;
    }

    public KeyPair getSign() {
        return sign;
    }

    public void setSign(KeyPair sign) {
        this.sign = sign;
    }

    public String getPublicSignKey(String encoding) {
        return sign != null ? encode(sign.getPublickey(), encoding) : null;
    }

    public String getPublicSignKey() {
        return getPublicSignKey("base58");
    }

    public String getPublicEncryptKey(String encoding) {
        return encrypt != null ? encode(encrypt.getPublickey(), encoding) : null;
    }

    public String getPublicEncryptKey() {
        return getPublicEncryptKey("base58");
    }

    public String sign(String message, String encoding) {
        if (sign == null || sign.getSecretkey() == null) {
            throw new RuntimeException("Unable to sign message; no secret sign key");
        }
        byte[] signature = CryptoUtil.crypto_sign_detached(message.getBytes(), sign.getSecretkey());
        return encode(signature, encoding);
    }

    public String sign(String message) {
        if (sign == null || sign.getSecretkey() == null) {
            throw new RuntimeException("Unable to sign message; no secret sign key");
        }
        byte[] signature = CryptoUtil.crypto_sign_detached(message.getBytes(), sign.getSecretkey());
        return encode(signature, "base58");
    }

    public boolean verify(String signature, String message, String encoding) {
        if (sign == null || sign.getPublickey() == null) {
            throw new RuntimeException("Unable to verify message; no public sign key");
        }

        byte[] rawSignature = decode(signature, encoding);

        return rawSignature.length == CryptoUtil.crypto_sign_bytes() &&
                sign.getPublickey().length == CryptoUtil.crypto_sign_publickeybytes() &&
                CryptoUtil.crypto_sign_verify_detached(rawSignature, message.getBytes(), sign.getPublickey());
    }

    public boolean verify(String signature, String message) {
        return verify(signature, message, "base58");
    }

    public byte[] encrypt(Account recipient, String message) {
        if (encrypt == null || encrypt.getSecretkey() == null) {
            throw new RuntimeException("Unable to encrypt message; no secret encryption key");
        }
        if (recipient.encrypt == null || recipient.encrypt.getPublickey() == null) {
            throw new RuntimeException("Unable to encrypt message; no public encryption key for recipient");
        }

        byte[] nonce = getNonce();

        byte[] retEncrypt = CryptoUtil.crypto_box(nonce, message.getBytes(), recipient.encrypt.getPublickey(), encrypt.getSecretkey());

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

        return CryptoUtil.crypto_box_open(nonce, encryptedMessage, encrypt.getPublickey(), sender.encrypt.getSecretkey());
    }

    protected byte[] getNonce() {
        return CryptoUtil.random_bytes(CryptoUtil.crypto_box_noncebytes());
    }

    protected static String encode(String string, String encoding) {
        if (encoding.equals("base58")) {
            string = StringUtil.base58Encode(string);
        }

        if (encoding.equals("base64")) {
            string = StringUtil.base64Encode(string);
        }

        return string;
    }

    protected static String encode(byte[] string, String encoding) {
        if (encoding.equals("base58")) {
            return StringUtil.base58Encode(string);
        }

        if (encoding.equals("base64")) {
            return StringUtil.base64Encode(string);
        }
        return null;
    }

    protected static String encode(String string) {
        return encode(string, "base58");
    }

    protected static String encode(byte[] string) {
        return encode(string, "base58");
    }

    protected static byte[] decode(String string, String encoding) {
        if (encoding.equals("base58")) {
            return StringUtil.base58Decode(string);
        }

        if (encoding.equals("base64")) {
            return StringUtil.base64Decode(string);
        }

        return null;
    }

    protected static byte[] decode(String string) {
        return decode(string, "base58");
    }
}