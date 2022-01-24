package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.Encoder;

import java.util.Arrays;
import java.util.List;

public class Lease extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static byte TYPE = 8;
    private final static List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 2, (byte) 3);
    private final long amount;
    private final Address recipient;

    public Lease(long amount, Address recipient, byte version) {
        super(TYPE, version, MINIMUM_FEE);

        checkVersion(version, SUPPORTED_VERSIONS);
        if (amount <= 0) throw new InvalidArgumentException("Invalid amount; should be greater than 0");

        this.amount = amount;
        this.recipient = recipient;
    }

    public Lease(long amount, Address recipient) {
        this(amount, recipient, (byte) 3);
    }

    public Lease(JsonObject json) {
        super(json);

        checkVersion(version, SUPPORTED_VERSIONS);

        this.amount = Long.parseLong(json.get("amount").toString());
        this.recipient = new Address(json.get("recipient").toString());
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) throw new BadMethodCallException("Sender public key not set");
        if (this.timestamp == 0) throw new BadMethodCallException("Timestamp not set");

        switch(version) {
            case (byte) 2: return toBinaryV2();
            case (byte) 3: return toBinaryV3();
            default: throw new IllegalArgumentException("Unknown version " + version);
        }
    }

    private byte[] toBinaryV2() {
        return Bytes.concat(
                new byte[]{this.type},                              // 1b
                new byte[]{this.version},                           // 1b
                new byte[]{0},                                      // 1b
                this.senderPublicKey.getRaw(),                      // 32b
                Encoder.base58Decode(this.recipient.getAddress()),  // 26b
                Longs.toByteArray(this.amount),                     // 8b
                Longs.toByteArray(this.fee),                        // 8b
                Longs.toByteArray(this.timestamp)                   // 8b
        );
    }

    private byte[] toBinaryV3() {
        return Bytes.concat(
                new byte[]{this.type},                              // 1b
                new byte[]{this.version},                           // 1b
                new byte[]{this.getNetwork()},                      // 1b
                Longs.toByteArray(this.timestamp),                  // 8b
                this.senderPublicKey.toBinary(),                    // 33b/34b
                Longs.toByteArray(this.fee),                        // 8b
                Encoder.base58Decode(this.recipient.getAddress()),  // 26b
                Longs.toByteArray(this.amount)                      // 8b
        );
    }
}
