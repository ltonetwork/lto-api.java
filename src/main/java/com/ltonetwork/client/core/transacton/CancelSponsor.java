package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.Encoder;

public class CancelSponsor extends Transaction {
    private final static long MINIMUM_FEE = 500_000_000;
    private final static byte TYPE = 19;
    private final static byte VERSION = 1;
    private final Address recipient;

    public CancelSponsor(Address recipient) {
        super(TYPE, VERSION, MINIMUM_FEE);

        this.recipient = recipient;
    }

    public CancelSponsor(JsonObject json) {
        super(json);
        this.recipient = new Address(json.get("recipient").toString());
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) {
            throw new BadMethodCallException("Sender public key not set");
        }

        if (this.timestamp == 0) {
            throw new BadMethodCallException("Timestamp not set");
        }

        return Bytes.concat(
                new byte[]{this.type},
                new byte[]{this.version},
                this.senderPublicKey.toRaw(),
                new byte[]{this.getNetwork()},
                Encoder.base58Decode(this.recipient.getAddress()),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee)
        );
    }
}
