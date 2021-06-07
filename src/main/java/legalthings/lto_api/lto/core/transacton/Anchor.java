package legalthings.lto_api.lto.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.lto.exceptions.InvalidArgumentException;
import legalthings.lto_api.utils.main.StringUtil;

import java.util.ArrayList;

public class Anchor extends Transaction {
    private final static long MINIMUM_FEE = 35_000_000;
    private final static int TYPE = 15;
    private final static int VERSION = 1;
    private final ArrayList<String> anchors;

    public Anchor(String hash, String encoding) {
        super(TYPE, VERSION, MINIMUM_FEE);
        anchors = new ArrayList<>();
        addHash(hash, encoding);
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
            for (Byte anc : StringUtil.base58Decode(anchor)) {
                anchorsBytes.add(anc);
            }
        }

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                StringUtil.base58Decode(this.senderPublicKey),
                Ints.toByteArray(anchors.size()),
                Bytes.toArray(anchorsBytes),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee)
        );
    }

    public void addHash(String hash, String encoding) {
        switch (encoding) {
            case "base58" -> anchors.add(hash);
            case "base64" -> anchors.add(StringUtil.base58Encode(new String(StringUtil.base64Decode(hash)), "base58"));
            case "raw" -> anchors.add(StringUtil.base58Encode(hash, "base58"));
//            TODO:
//            case "hex" ->
            default -> throw new InvalidArgumentException(String.format("Failed to encode to %s", encoding));
        }
    }

    public String getHash(String encoding) {
        if (anchors.size() != 1)
            throw new BadMethodCallException("Method 'getHash' can't be used on a multi-anchor tx");

        return switch (encoding) {
            case "base58" -> this.anchors.get(0);
            case "base64" -> StringUtil.base64Encode(new String(StringUtil.base58Decode(this.anchors.get(0))));
            case "raw" -> new String(StringUtil.base58Decode(this.anchors.get(0)));
//            TODO:
//            case "hex" ->
            default -> throw new InvalidArgumentException(String.format("Failed to encode to %s", encoding));
        };
    }

    public String[] getHashes(String encoding) {
        if (encoding.equals("base58")) return this.anchors.toArray(new String[0]);

        String[] hashes = new String[this.anchors.size()];
        for (int i = 0; i < this.anchors.size(); i++) {
            hashes[i] = switch (encoding) {
                case "base64" -> StringUtil.base64Encode(new String(StringUtil.base58Decode(this.anchors.get(0))));
                case "raw" -> new String(StringUtil.base58Decode(this.anchors.get(0)));
//            TODO:
//            case "hex" ->
                default -> throw new InvalidArgumentException(String.format("Failed to encode to %s", encoding));
            };
        }

        return hashes;
    }

}
