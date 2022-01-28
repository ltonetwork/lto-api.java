package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.Encoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Anchor extends Transaction {
    private final static long MINIMUM_FEE = 35_000_000;
    private final static byte TYPE = 15;
    private final static List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 1, (byte) 3);
    private final ArrayList<String> anchors;

    public Anchor(String hash, Encoding encoding, byte version) {
        super(TYPE, version, MINIMUM_FEE);

        checkVersion(SUPPORTED_VERSIONS);

        anchors = new ArrayList<>();
        addHash(hash, encoding);
    }

    public Anchor(String hash, Encoding encoding) {
        this(hash, encoding, (byte) 3);
    }

    public Anchor(JsonObject json) {
        super(json);

        checkVersion(SUPPORTED_VERSIONS);

        JsonObject jsonAnchors = new JsonObject(json.get("anchors").toString(), true);
        ArrayList<String> anchors = new ArrayList<>();
        for (int i = 0; i < jsonAnchors.length(); i++) {
            anchors.add(jsonAnchors.get(i));
        }

        this.anchors = anchors;
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

    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.put("anchors", anchors.toString());

        return json;
    }

    public void addHash(String hash, Encoding encoding) {
        anchors.add(Encoder.base58Encode(Encoder.decode(hash, encoding)));
    }

    public String getHash(Encoding encoding) {
        if (anchors.size() != 1)
            throw new BadMethodCallException("Method 'getHash' can't be used on a multi-anchor tx");

        return Encoder.encode(
                Encoder.base58Decode(this.anchors.get(0)),
                encoding
        );
    }

    public String[] getHashes(Encoding encoding) {
        if (encoding == Encoding.BASE58) return this.anchors.toArray(new String[0]);

        String[] hashes = new String[this.anchors.size()];
        for (int i = 0; i < this.anchors.size(); i++) {
            hashes[i] = Encoder.encode(
                    Encoder.base58Decode(this.anchors.get(i)),
                    encoding
            );
        }

        return hashes;
    }

    // includes each anchor's length and value
    private ArrayList<Byte> anchorsBytes() {
        ArrayList<Byte> anchorsBytes = new ArrayList<>();
        for (String anchor : anchors) {
            byte[] decodedAnchor = Encoder.base58Decode(anchor);
            byte[] ancLen = Shorts.toByteArray((short) decodedAnchor.length);
            anchorsBytes.add(ancLen[0]);
            anchorsBytes.add(ancLen[1]);
            for (Byte anc : decodedAnchor) {
                anchorsBytes.add(anc);
            }
        }
        return anchorsBytes;
    }

    private byte[] toBinaryV1() {
        return Bytes.concat(
                new byte[]{this.type},                      // 1b
                new byte[]{this.version},                   // 1b
                this.senderPublicKey.getRaw(),              // 32b
                Shorts.toByteArray((short) anchors.size()), // 2b
                Bytes.toArray(anchorsBytes()),              // (2b + mb)*nb
                Longs.toByteArray(this.timestamp),          // 8b
                Longs.toByteArray(this.fee)                 // 8b
        );
    }

    private byte[] toBinaryV3() {
        return Bytes.concat(
                new byte[]{this.type},                      // 1b
                new byte[]{this.version},                   // 1b
                new byte[]{this.getNetwork()},              // 1b
                Longs.toByteArray(this.timestamp),          // 8b
                this.senderPublicKey.toBinary(),            // 33b|34b
                Longs.toByteArray(this.fee),                // 8b
                Shorts.toByteArray((short) anchors.size()), // 2b
                Bytes.toArray(anchorsBytes())               // (2b + mb)*nb
        );
    }
}
