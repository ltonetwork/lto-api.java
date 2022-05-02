package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.Encoder;

import java.util.Arrays;
import java.util.List;

public class CancelSponsor extends Transaction {
    private final static long MINIMUM_FEE = 500_000_000;
    private final static byte TYPE = 19;
    private final static List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 1, (byte) 3);
    private final Address recipient;

    public CancelSponsor(Address recipient, byte version) {
        super(TYPE, version, MINIMUM_FEE);

        checkVersion(SUPPORTED_VERSIONS);

        this.recipient = recipient;
    }

    public CancelSponsor(Address recipient) {
        this(recipient, (byte) 3);
    }

    public CancelSponsor(JsonObject json) {
        super(json);

        checkVersion(SUPPORTED_VERSIONS);

        this.recipient = new Address(json.get("recipient").toString());
    }

    public byte[] toBinary() {
        checkToBinary();

        switch (version) {
            case (byte) 1:
                return toBinaryV1();
            case (byte) 3:
                return toBinaryV3();
            default:
                throw new IllegalArgumentException("Unknown version " + version);
        }
    }

    private byte[] toBinaryV1() {
        return Bytes.concat(
                new byte[]{this.type},                              // 1b
                new byte[]{this.version},                           // 1b
                new byte[]{this.getNetwork()},                      // 1b
                this.senderPublicKey.getRaw(),                      // 32b
                Encoder.base58Decode(this.recipient.getAddress()),  // 26b
                Longs.toByteArray(this.timestamp),                  // 8b
                Longs.toByteArray(this.fee)                         // 8b
        );
    }

    private byte[] toBinaryV3() {
        return Bytes.concat(
                new byte[]{this.type},                              // 1b
                new byte[]{this.version},                           // 1b
                new byte[]{this.getNetwork()},                      // 1b
                Longs.toByteArray(this.timestamp),                  // 8b
                this.senderPublicKey.toBinary(),                    // 33b|34b
                Longs.toByteArray(this.fee),                        // 8b
                Encoder.base58Decode(this.recipient.getAddress())   // 26b
        );
    }
}
