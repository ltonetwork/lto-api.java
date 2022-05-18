package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.Encoder;

import java.util.Arrays;
import java.util.List;

public class Burn extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static byte TYPE = 21;
    private final static List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 3);
    private final long amount;

    public Burn(int amount, byte version) {
        super(TYPE, version, MINIMUM_FEE);
        checkVersion(SUPPORTED_VERSIONS);
        if (amount <1) throw new InvalidArgumentException("Minimum burn amount = 1");

        this.amount = amount;
    }
    public Burn(int amount) {
        this(amount, (byte) 3);
    }

    public Burn(JsonObject json) {
        super(json);
        long am = Long.parseLong(json.get("amount").toString());

        checkVersion(SUPPORTED_VERSIONS);
        if (am < 1) throw new InvalidArgumentException("Minimum burn amount = 1");

        this.amount = am;
    }

    public byte[] toBinary() {
        checkToBinary();

        switch (version) {
            case (byte) 3:
                return toBinaryV3();
            default:
                throw new IllegalArgumentException("Unknown version " + version);
        }
    }
    private byte[] toBinaryV3() {
        return Bytes.concat(
                new byte[]{this.type},                              // 1b
                new byte[]{this.version},                           // 1b
                new byte[]{this.getNetwork()},                      // 1b
                Longs.toByteArray(this.timestamp),                  // 8b
                this.senderPublicKey.toBinary(),                    // 33b|34b
                Longs.toByteArray(this.fee),                        // 8b
                Longs.toByteArray(this.amount),                     // 8b
        );
    }
}
