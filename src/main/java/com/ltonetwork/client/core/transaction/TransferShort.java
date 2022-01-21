package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.types.Address;

public class TransferShort {
    private final Address recipient;
    private final long amount;

    public TransferShort(Address recipient, long amount) {
        this.recipient = recipient;
        this.amount = amount;
    }

    public Address getRecipient() {
        return recipient;
    }

    public long getAmount() {
        return amount;
    }
}
