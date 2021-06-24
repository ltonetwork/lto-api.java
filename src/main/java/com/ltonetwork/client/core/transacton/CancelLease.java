package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.utils.Encoder;
import com.ltonetwork.client.utils.JsonObject;

public class CancelLease extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static int TYPE = 9;
    private final static int VERSION = 2;
    private final long leaseId;
//    private final Lease lease;

    public CancelLease(int leaseId) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.leaseId = leaseId;
    }

    public CancelLease(JsonObject json) {
        super(json);
        this.leaseId = (long) json.get("id");
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
                new byte[this.getNetwork()],
                Encoder.base58Decode(this.senderPublicKey),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee)
        );
    }
}
