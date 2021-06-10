package legalthings.lto_api.lto.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.lto.exceptions.InvalidArgumentException;
import legalthings.lto_api.utils.main.CryptoUtil;
import legalthings.lto_api.utils.main.Encoder;

public class Transfer extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static int TYPE = 4;
    private final static int VERSION = 2;
    private final long amount;
    private String attachment = "";
    private final String recipient;

    public Transfer(int amount, String recipient) {
        super(TYPE, VERSION, MINIMUM_FEE);

        if (amount <= 0) {
            throw new InvalidArgumentException("Invalid amount; should be greater than 0");
        }

        if (!CryptoUtil.isValidAddress(recipient, "base58")) {
            throw new InvalidArgumentException("Invalid recipient address; is it base58 encoded?");
        }

        this.amount = amount;
        this.recipient = recipient;
    }

    public void setAttachment(String message, String encoding) {
        this.attachment = Encoder.fromXStringToBase58String(message, encoding);
    }

    public void setAttachment(String message) {
        setAttachment(message, "raw");
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) {
            throw new BadMethodCallException("Sender public key not set");
        }

        if (this.timestamp == 0) {
            throw new BadMethodCallException("Timestamp not set");
        }

        byte[] binaryAttachment = Encoder.base58Decode(this.attachment);

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                Encoder.base58Decode(this.senderPublicKey),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.amount),
                Longs.toByteArray(this.fee),
                Encoder.base58Decode(this.recipient),
                Ints.toByteArray(attachment.length()),
                binaryAttachment
        );
    }
}
