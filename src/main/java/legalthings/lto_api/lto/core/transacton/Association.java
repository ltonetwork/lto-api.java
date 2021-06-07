package legalthings.lto_api.lto.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.lto.exceptions.InvalidArgumentException;
import legalthings.lto_api.utils.main.StringUtil;

public class Association extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static int TYPE = 16;
    private final static int VERSION = 1;
    private final String party;
    private final int associationType;
    private final String hash;

    public Association(String party, int type, String hash, String encoding) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.party = party;
        this.associationType = type;
        this.hash = switch (encoding) {
            case "base58" -> hash;
            case "base64" -> StringUtil.base58Encode(new String(StringUtil.base64Decode(hash)));
            case "raw" -> StringUtil.base58Encode(hash);
//            TODO:
//            case "hex" ->
            default -> throw new InvalidArgumentException(String.format("Failed to encode to %s", encoding));
        };
    }

    public Association(String party, int type) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.party = party;
        this.associationType = type;
        this.hash = "";
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
            byte[] rawHash = StringUtil.base58Decode(this.hash);
            hashByte = Bytes.concat(
                    Ints.toByteArray(1),
                    Ints.toByteArray(rawHash.length),
                    rawHash);
        }

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                StringUtil.base58Decode(this.senderPublicKey),
                this.getNetwork(),
                StringUtil.base58Decode(this.party),
                Ints.toByteArray(associationType),
                hashByte,
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee)
        );
    }

    public String getHash(String encoding) {
        return switch (encoding) {
            case "base58" -> this.hash;
            case "base64" -> StringUtil.base58Encode(new String(StringUtil.base64Decode(this.hash)));
            case "raw" -> new String(StringUtil.base58Encode(this.hash));
//            TODO:
//            case "hex" ->
            default -> throw new InvalidArgumentException(String.format("Failed to encode to %s", encoding));
        };
    }

    public String getHash() {
        return getHash("base58");
    }
}
