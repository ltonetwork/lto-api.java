package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class CancelLease extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static byte TYPE = 9;
    private final static List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 2, (byte) 3);
    private final String leaseId;

    public CancelLease(String leaseId, byte version) {
        super(TYPE, version, MINIMUM_FEE);

        checkVersion(SUPPORTED_VERSIONS);

        this.leaseId = leaseId;
    }

    public CancelLease(String leaseId) {
        this(leaseId, (byte) 3);
    }

    public CancelLease(JsonObject json) {
        super(json);

        checkVersion(SUPPORTED_VERSIONS);

        this.leaseId = json.get("leaseId").toString();
    }

    public byte[] toBinary() {
        checkToBinary();

        switch(version) {
            case (byte) 2: return toBinaryV2();
            case (byte) 3: return toBinaryV3();
            default: throw new IllegalArgumentException("Unknown version " + version);
        }
    }

    private byte[] toBinaryV2() {
        return Bytes.concat(
                new byte[]{this.type},                      // 1b
                new byte[]{this.version},                   // 1b
                new byte[]{this.getNetwork()},              // 1b
                this.senderPublicKey.getRaw(),              // 32b
                Longs.toByteArray(this.fee),                // 8b
                Longs.toByteArray(this.timestamp),          // 8b
                leaseId.getBytes(StandardCharsets.UTF_8)    // 32b
        );
    }

    private byte[] toBinaryV3() {
        return Bytes.concat(
                new byte[]{this.type},                      // 1b
                new byte[]{this.version},                   // 1b
                new byte[]{this.getNetwork()},              // 1b
                Longs.toByteArray(this.timestamp),          // 8b
                this.senderPublicKey.toBinary(),            // 33b/34b
                Longs.toByteArray(this.fee),                // 8b
                leaseId.getBytes(StandardCharsets.UTF_8)    // 32b
        );
    }
}
