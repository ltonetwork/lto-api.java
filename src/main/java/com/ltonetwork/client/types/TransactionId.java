package com.ltonetwork.client.types;

import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.utils.Encoder;

public class TransactionId {
    private final String value;

    public TransactionId(String value) {
        if(value.length() != 43) throw new BadMethodCallException("Transaction id should be 43 chars long");
        if(!Encoder.isBase58Encoded(value)) throw new BadMethodCallException("Transaction id is not base58 encoded");

        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
