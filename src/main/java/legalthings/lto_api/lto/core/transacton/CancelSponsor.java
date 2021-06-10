package legalthings.lto_api.lto.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.utils.main.StringUtil;

public class CancelSponsor extends Transaction {
    private final static long MINIMUM_FEE = 500_000_000;
    private final static int TYPE = 19;
    private final static int VERSION = 1;
    private final String recipient;

    public CancelSponsor(String recipient) {
        super(TYPE, VERSION, MINIMUM_FEE);
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
                new byte[this.getNetwork()],
                StringUtil.base58Decode(this.senderPublicKey),
                StringUtil.base58Decode(this.recipient),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee)
        );
    }
}
