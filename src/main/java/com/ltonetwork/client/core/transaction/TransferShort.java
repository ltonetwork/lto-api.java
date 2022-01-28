package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.JsonObject;

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

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("recipient", recipient.getAddress());
        json.put("amount", String.valueOf(amount));

        return json;
    }
}
