package com.ltonetwork.client.types;

import com.ltonetwork.client.utils.CryptoUtil;
import com.ltonetwork.client.utils.Encoder;

public class Signature {
    private byte[] value;

    /** Create a signature from bytes.*/
    public Signature(byte[] value) {
        this.value = value;
    }

    /** Create a signature from String.*/
    public Signature(String value, Encoding encoding) {
        switch (encoding) {
            case BASE58 -> this.value = Encoder.base58Decode(value);
            case BASE64 -> this.value = Encoder.base64Decode(value);
            case HEX -> this.value = Encoder.hexDecode(value);
        }
    }

    /** Sign message with a key and create signature.*/
    public Signature(byte[] message, Key secretkey) {
        this.value = CryptoUtil.crypto_sign_detached(message, secretkey.getValueBytes());
    }

    public byte[] byteArray(){
        return value;
    }

    public String base58(){
        return Encoder.base58Encode(value);
    }

    public String base64(){
        return Encoder.base64Encode(value);
    }

    public String hex(){
        return Encoder.hexEncode(value);
    }

    public boolean verify(Key publickey, String message){
        return this.value.length == CryptoUtil.crypto_sign_bytes() &&
                publickey.getValueBytes().length == CryptoUtil.crypto_sign_publickeybytes() &&
                CryptoUtil.crypto_sign_verify_detached(
                        this.value,
                        message.getBytes(),
                        publickey.getValueBytes()
                );
    }

    public boolean verify(KeyPair kp, String message){
        return verify(kp.getPublickey(), message);
    }

    public boolean verify(Key publickey, byte[] message){
        return this.value.length == CryptoUtil.crypto_sign_bytes() &&
                publickey.getValueBytes().length == CryptoUtil.crypto_sign_publickeybytes() &&
                CryptoUtil.crypto_sign_verify_detached(
                        this.value,
                        message,
                        publickey.getValueBytes()
                );
    }

    public boolean verify(KeyPair kp, byte[] message){
        return verify(kp.getPublickey(), message);
    }

}
