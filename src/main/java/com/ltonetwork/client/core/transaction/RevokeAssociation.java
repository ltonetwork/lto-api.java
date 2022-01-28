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

public class RevokeAssociation extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static byte TYPE = 17;
    private final static List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 1, (byte) 3);
    private final Address party;
    private final int associationType;
    private String hash;

    public RevokeAssociation(Address party, int type, String hash, Encoding encoding, byte version) {
        super(TYPE, version, MINIMUM_FEE);

        if (!SUPPORTED_VERSIONS.contains(version))
            throw new IllegalArgumentException("Unknown version, supported versions are: " + SUPPORTED_VERSIONS);

        this.party = party;
        this.associationType = type;
        this.hash = Encoder.base58Encode(Encoder.decode(hash, encoding));
    }

    public RevokeAssociation(Address party, int type, String hash, Encoding encoding) {
        this(party, type, hash, encoding, (byte) 3);
    }

    public RevokeAssociation(Address party, int type, byte version) {
        super(TYPE, version, MINIMUM_FEE);

        if (!SUPPORTED_VERSIONS.contains(version))
            throw new IllegalArgumentException("Unknown version, supported versions are: " + SUPPORTED_VERSIONS);

        this.party = party;
        this.associationType = type;
    }

    public RevokeAssociation(Address party, int type) {
        this(party, type, (byte) 3);
    }

    public RevokeAssociation(JsonObject json) {
        super(json);

        byte versionFromJson = Byte.parseByte(json.get("version").toString());

        if (!SUPPORTED_VERSIONS.contains(versionFromJson))
            throw new IllegalArgumentException("Unknown version, supported versions are: " + SUPPORTED_VERSIONS);

        this.party = new Address(json.get("party").toString());
        this.associationType = Integer.parseInt(json.get("associationType").toString());
        if (json.has("hash")) this.hash = json.get("hash").toString();
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) throw new BadMethodCallException("Sender public key not set");
        if (this.timestamp == 0) throw new BadMethodCallException("Timestamp not set");

        switch (version) {
            case (byte) 1:
                return toBinaryV1();
            case (byte) 3:
                return toBinaryV3();
            default:
                throw new IllegalArgumentException("Unknown version " + version);
        }
    }

    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.put("associationType", String.valueOf(associationType));
        json.put("recipient", party.getAddress());

        if (!hash.equals("")) json.put("hash", hash);

        return json;
    }

    public String getHash(Encoding encoding) {
        if (hash == null)
            throw new BadMethodCallException("Can't get hash; missing");
        return Encoder.encode(
                Encoder.base58Decode(hash),
                encoding
        );
    }

    public String getHash() {
        return getHash(Encoding.BASE58);
    }

    private byte[] toBinaryV1() {
        return Bytes.concat(
                new byte[]{this.type},                          // 1b
                new byte[]{this.version},                       // 1b
                new byte[]{this.getNetwork()},                  // 1b
                this.senderPublicKey.getRaw(),                  // 32b
                Encoder.base58Decode(this.party.getAddress()),  // 26b
                Ints.toByteArray(associationType),              // 4b
                hashToBinary(),                                 // 1b + (2b + nb)
                Longs.toByteArray(this.timestamp),              // 8b
                Longs.toByteArray(this.fee)                     // 8b
        );
    }

    private byte[] toBinaryV3() {
        return Bytes.concat(
                new byte[]{this.type},                          // 1b
                new byte[]{this.version},                       // 1b
                new byte[]{this.getNetwork()},                  // 1b
                Longs.toByteArray(this.timestamp),              // 8b
                this.senderPublicKey.toBinary(),                // 33b|34b
                Longs.toByteArray(this.fee),                    // 8b
                Encoder.base58Decode(this.party.getAddress()),  // 26b
                Ints.toByteArray(associationType),              // 4b
                hashToBinary()                                  // 1b + (2b + nb)
        );
    }

    private byte[] hashToBinary() {
        if (hash != null) {
            byte[] rawHash = Encoder.base58Decode(this.hash);
            return Bytes.concat(
                    new byte[]{(byte) 1},
                    Shorts.toByteArray((short) rawHash.length),
                    rawHash);
        } else {
            return new byte[]{(byte) 0};
        }
    }
}
