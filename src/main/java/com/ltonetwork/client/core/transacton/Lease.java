package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.utils.Encoder;
import com.ltonetwork.client.types.JsonObject;

import java.nio.charset.StandardCharsets;

public class Lease extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static byte TYPE = 8;
    private final static byte VERSION = 2;
    private final long amount;
    private final Address recipient;

    public Lease(long amount, Address recipient) {
        super(TYPE, VERSION, MINIMUM_FEE);

        if (amount <= 0) {
            throw new InvalidArgumentException("Invalid amount; should be greater than 0");
        }

        this.amount = amount;
        this.recipient = recipient;
    }

    public Lease(JsonObject json) {
        super(json);
        this.amount = (long) json.get("amount");
        this.recipient = new Address((String) json.get("recipient"));
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) {
            throw new BadMethodCallException("Sender public key not set");
        }

        if (this.timestamp == 0) {
            throw new BadMethodCallException("Timestamp not set");
        }

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                this.senderPublicKey.toBase58().getBytes(StandardCharsets.UTF_8),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.amount),
                Longs.toByteArray(this.fee),
                Encoder.base58Decode(this.recipient.getAddress())
        );
    }
}
