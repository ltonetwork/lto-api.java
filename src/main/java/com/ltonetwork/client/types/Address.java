package com.ltonetwork.client.types;

import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.utils.CryptoUtil;
import com.ltonetwork.client.utils.Encoder;

import java.nio.charset.StandardCharsets;

public class Address {
    private final String address;
    private final byte chainId;

    public Address(String address) {
        if (!CryptoUtil.isValidAddress(address, Encoding.BASE58)) {
            throw new InvalidArgumentException("Address should be base58 encoded");
        }
        this.address = address;
        this.chainId = fetchChainIdFromAddress(address);
    }

    public Address(String address, Encoding encoding) {
        if (encoding == Encoding.BASE58 && !CryptoUtil.isValidAddress(address, Encoding.BASE58)) {
            throw new InvalidArgumentException("Address is not properly base58 encoded");
        }
        if (encoding == Encoding.BASE64 && !CryptoUtil.isValidAddress(address, Encoding.BASE64)) {
            throw new InvalidArgumentException("Address is not properly base64 encoded");
        }

        switch (encoding) {
            case BASE58: {
                if (!CryptoUtil.isValidAddress(address, Encoding.BASE58)) {
                    throw new InvalidArgumentException("Address is not properly base58 encoded");
                }
                this.address = address;
                this.chainId = fetchChainIdFromAddress(address);
                break;
            }
            case BASE64: {
                if (!CryptoUtil.isValidAddress(address, Encoding.BASE64)) {
                    throw new InvalidArgumentException("Address is not properly base64 encoded");
                }
                this.address = Encoder.base58Encode(Encoder.base64Decode(address, StandardCharsets.UTF_8));
                this.chainId = fetchChainIdFromAddress(address);
                break;
            }
            default:
                throw new InvalidArgumentException("Address is field supports only base58 and base64 encodings");
        }
    }

    public String getAddress() {
        return address;
    }

    public String getAddressBase64() {
        return Encoder.base64Encode(Encoder.base58Decode(address, StandardCharsets.UTF_8));
    }

    public byte getChainId() {
        return chainId;
    }

    private byte fetchChainIdFromAddress(String address) {
        return Encoder.base58Decode(address)[1];
    }
}
