package com.ltonetwork.client.core.transacton;

public class TransferShort {
    private final String recipient;
    private final long amount;

    public TransferShort(String recipient, long amount) {
        this.recipient = recipient;
        this.amount = amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public long getAmount() {
        return amount;
    }
}
