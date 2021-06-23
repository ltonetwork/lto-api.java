package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.Encoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Anchor extends Transaction {
    private final static long MINIMUM_FEE = 35_000_000;
    private final static byte TYPE = 15;
    private final static byte VERSION = 1;
    private final ArrayList<String> anchors;

    public Anchor(String hash, Encoding encoding) {
        super(TYPE, VERSION, MINIMUM_FEE);
        anchors = new ArrayList<>();
        addHash(hash, encoding);
    }

    public Anchor(JsonObject json) {
        super(json);
        JsonObject jsonAnchors = new JsonObject(json.get("anchors").toString(), true);
        ArrayList<String> anchors = new ArrayList<>();

        for (int i = 0; i < jsonAnchors.length(); i++) {
            anchors.add(jsonAnchors.get(i));
        }

        this.anchors = anchors;
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) {
            throw new BadMethodCallException("Sender public key not set");
        }

        if (this.timestamp == 0) {
            throw new BadMethodCallException("Timestamp not set");
        }

        ArrayList<Byte> anchorsBytes = new ArrayList<>();
        for (String anchor : anchors) {
            for (Byte anc : Encoder.base58Decode(anchor)) {
                anchorsBytes.add(anc);
            }
        }

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                this.senderPublicKey.toBase58().getBytes(StandardCharsets.UTF_8),
                Ints.toByteArray(anchors.size()),
                Bytes.toArray(anchorsBytes),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee)
        );
    }

    public void addHash(String hash, Encoding encoding) {
        anchors.add(Encoder.base58Encode(Encoder.decode(hash, encoding)));
    }

    public String getHash(Encoding encoding) {
        if (anchors.size() != 1)
            throw new BadMethodCallException("Method 'getHash' can't be used on a multi-anchor tx");

        return Encoder.encode(
                Encoder.base58Decode(this.anchors.get(0), StandardCharsets.UTF_8),
                encoding
        );
    }

    public String[] getHashes(Encoding encoding) {
        if (encoding == Encoding.BASE58) return this.anchors.toArray(new String[0]);

        String[] hashes = new String[this.anchors.size()];
        for (int i = 0; i < this.anchors.size(); i++) {
            hashes[i] = Encoder.encode(
                    Encoder.base58Decode(this.anchors.get(i), StandardCharsets.UTF_8),
                    encoding
            );
        }

        return hashes;
    }

}
