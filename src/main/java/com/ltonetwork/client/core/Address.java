package com.ltonetwork.client.core;

public class Address {
    private final byte[] address;
    private byte chainId;

    public Address(byte[] address, byte chainId) {
        this.address = address;
        this.chainId = chainId;
    }

    public Address(byte[] address) {
        this.address = address;
    }

    public byte[] getAddress() {
        return address;
    }

    public byte getChainId() {
        return chainId;
    }
}
