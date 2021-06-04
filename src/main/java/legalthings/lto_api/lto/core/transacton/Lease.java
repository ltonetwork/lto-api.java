package legalthings.lto_api.lto.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.lto.exceptions.InvalidArgumentException;
import legalthings.lto_api.utils.main.StringUtil;

public class Lease extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static int TYPE = 8;
    private final static int VERSION = 2;
    private final long amount;
    private final String recipient;

    public Lease(int amount, String recipient) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.amount = amount;
        this.recipient = recipient;
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) {
            throw new BadMethodCallException("Sender public key not set");
        }

        if (this.timestamp == 0) {
            throw new BadMethodCallException("Timestamp not set");
        }

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                StringUtil.base58Decode(this.senderPublicKey),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.amount),
                Longs.toByteArray(this.fee),
                StringUtil.base58Decode(this.recipient)
        );
    }
}
