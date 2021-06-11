package com.ltonetwork.client.core;

public class Address {
    private final byte[] address;
    private final byte chainId;

    public Address(byte[] address, byte chainId) {
        this.address = address;
        this.chainId = chainId;
    }

    public byte[] getAddress() {
        return address;
    }

    public byte getChainId() {
        return chainId;
    }
}
