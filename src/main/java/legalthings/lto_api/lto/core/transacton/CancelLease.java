package legalthings.lto_api.lto.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.utils.main.StringUtil;

public class CancelLease extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static int TYPE = 9;
    private final static int VERSION = 2;
    private final long leaseId;
//    private final Lease lease;

    public CancelLease(int leaseId) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.leaseId = leaseId;
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
                this.getNetwork(),
                StringUtil.base58Decode(this.senderPublicKey),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee)
        );
    }
}