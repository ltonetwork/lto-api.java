package legalthings.lto_api.lto.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.lto.exceptions.InvalidArgumentException;
import legalthings.lto_api.utils.main.CryptoUtil;
import legalthings.lto_api.utils.main.Encoder;

public class Association extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static int TYPE = 16;
    private final static int VERSION = 1;
    private final String party;
    private final int associationType;
    private final String hash;

    public Association(String party, int type, String hash, String encoding) {
        super(TYPE, VERSION, MINIMUM_FEE);

        if (!CryptoUtil.isValidAddress(party, "base58")) {
            throw new InvalidArgumentException("Invalid party address; is it base58 encoded?");
        }

        this.party = party;
        this.associationType = type;
        this.hash = Encoder.fromXStringToBase58String(hash, encoding);
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
            byte[] rawHash = Encoder.base58Decode(this.hash);
            hashByte = Bytes.concat(
                    Ints.toByteArray(1),
                    Ints.toByteArray(rawHash.length),
                    rawHash);
        }

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                Encoder.base58Decode(this.senderPublicKey),
                new byte[this.getNetwork()],
                Encoder.base58Decode(this.party),
                Ints.toByteArray(associationType),
                hashByte,
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee)
        );
    }

    public String getHash(String encoding) {
        return Encoder.fromBase58StringToXString(this.hash, encoding);
    }

    public String getHash() {
        return getHash("base58");
    }
}
