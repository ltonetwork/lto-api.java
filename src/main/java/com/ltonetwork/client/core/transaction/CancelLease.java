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
        this.leaseId = leaseId;
    }

    public CancelLease(String leaseId) {
        super(TYPE, (byte) 3, MINIMUM_FEE);

        if(!SUPPORTED_VERSIONS.contains(version))
            throw new IllegalArgumentException("Unknown version, supported versions are: " + SUPPORTED_VERSIONS);

        this.leaseId = leaseId;
    }

    public CancelLease(JsonObject json) {
        super(json);

        if(!SUPPORTED_VERSIONS.contains(version))
            throw new IllegalArgumentException("Unknown version, supported versions are: " + SUPPORTED_VERSIONS);

        this.leaseId = json.get("leaseId").toString();
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
                this.senderPublicKey.getRaw(),
                new byte[]{this.getNetwork()},
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee),
                leaseId.getBytes(StandardCharsets.UTF_8)
        );
    }
}
