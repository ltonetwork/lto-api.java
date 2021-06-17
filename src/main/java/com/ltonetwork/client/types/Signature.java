package com.ltonetwork.client.types;

import com.ltonetwork.client.utils.CryptoUtil;
import com.ltonetwork.client.utils.Encoder;

public class Signature {
    private byte[] value;

    public Signature(byte[] value) {
        this.value = value;
    }

    public Signature(String value, Encoding encoding) {
        switch (encoding) {
            case BASE58 -> this.value = Encoder.base58Decode(value);
            case BASE64 -> this.value = Encoder.base64Decode(value);
            case HEX -> this.value = Encoder.hexDecode(value);
        }
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
