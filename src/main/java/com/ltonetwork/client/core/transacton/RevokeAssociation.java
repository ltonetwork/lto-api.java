package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.Encoder;

import java.nio.charset.StandardCharsets;

public class RevokeAssociation extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static byte TYPE = 17;
    private final static byte VERSION = 1;
    private final Address party;
    private final int associationType;
    private String hash;

    public RevokeAssociation(Address party, int type, String hash, Encoding encoding) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.party = party;
        this.associationType = type;
        this.hash = Encoder.base58Encode(Encoder.decode(hash, encoding));
    }

    public RevokeAssociation(Address party, int type) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.party = party;
        this.associationType = type;
    }

    public RevokeAssociation(JsonObject json) {
        super(json);
        this.party = new Address(json.get("party").toString());
        this.associationType = Integer.parseInt(json.get("associationType").toString());
        if (json.has("hash")) this.hash = json.get("hash").toString();
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) {
            throw new BadMethodCallException("Sender public key not set");
        }

        if (this.timestamp == 0) {
            throw new BadMethodCallException("Timestamp not set");
        }

        byte[] ret = Bytes.concat(
                new byte[]{this.type},
                new byte[]{this.version},
                this.senderPublicKey.toRaw(),
                new byte[]{this.getNetwork()},
                Encoder.base58Decode(this.party.getAddress()),
                Ints.toByteArray(associationType)
        );

        if (hash != null) {
            byte[] rawHash = Encoder.base58Decode(this.hash);
            ret = Bytes.concat(
                    ret,
                    new byte[]{(byte) 1},
                    Shorts.toByteArray((short) rawHash.length),
                    rawHash);
        } else {
            ret = Bytes.concat(
                    ret,
                    new byte[]{(byte) 0});
        }

        return Bytes.concat(
                ret,
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee)
        );
    }

    public String getHash(Encoding encoding) {
        if (hash == null)
            throw new BadMethodCallException("Can't get hash; missing");
        return Encoder.encode(
                Encoder.base58Decode(hash, StandardCharsets.UTF_8),
                encoding
        );
    }

    public String getHash() {
        return getHash(Encoding.BASE58);
    }
}
