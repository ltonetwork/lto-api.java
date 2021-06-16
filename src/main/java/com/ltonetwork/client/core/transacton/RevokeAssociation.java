package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.utils.Encoder;
import com.ltonetwork.client.utils.JsonObject;

import java.nio.charset.StandardCharsets;

public class RevokeAssociation extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static int TYPE = 17;
    private final static int VERSION = 1;
    private final String party;
    private final int associationType;
    private final String hash;

    public RevokeAssociation(String party, int type, String hash, Encoder.Encoding encoding) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.party = party;
        this.associationType = type;
        this.hash = Encoder.base58Encode(Encoder.decode(hash, encoding));
    }

    public RevokeAssociation(String party, int type) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.party = party;
        this.associationType = type;
        this.hash = "";
    }

    public RevokeAssociation(JsonObject json) {
        super(json);
        this.party = (String) json.get("party");
        this.associationType = (int) json.get("associationType");
        this.hash = (String) json.get("hash");
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) {
            throw new BadMethodCallException("Sender public key not set");
        }

        if (this.timestamp == 0) {
            throw new BadMethodCallException("Timestamp not set");
        }

        byte[] hashByte;

        if (hash.equals("")) {
            hashByte = Ints.toByteArray(0);
        } else {
            byte[] rawHash = Encoder.base58Decode(this.hash);
            hashByte = Bytes.concat(
                    Ints.toByteArray(1),
                    Ints.toByteArray(rawHash.length),
                    rawHash);
        }

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                this.senderPublicKey.toBase58().getBytes(StandardCharsets.UTF_8),
                new byte[this.getNetwork()],
                Encoder.base58Decode(this.party),
                Ints.toByteArray(associationType),
                hashByte,
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee)
        );
    }
}
