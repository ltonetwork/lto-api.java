package legalthings.lto_api.lto.core.transacton;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.lto.exceptions.InvalidArgumentException;
import legalthings.lto_api.utils.main.StringUtil;

import com.google.common.primitives.Bytes;

public class Transfer extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static int TYPE = 4;
    private final static int VERSION = 2;
    private final long amount;
    private String attachment = "";
    private final String recipient;

    public Transfer(String id, int amount, String attachment, String[] proofs, String recipient) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.amount = amount;
        this.attachment = attachment;
        this.recipient = recipient;
    }

    public void setAttachment(String message, String encoding) {
        this.attachment = switch (encoding) {
            case "base58" -> message;
            case "base64" -> StringUtil.base58Encode(new String(StringUtil.base64Decode(message)), "base58");
            case "raw" -> StringUtil.base58Encode(message, "base58");
//            TODO:
//            case "hex" ->
            default -> throw new InvalidArgumentException(String.format("Failed to encode to %s", encoding));
        };
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

        byte[] binaryAttachment = StringUtil.base58Decode(this.attachment);

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                StringUtil.base58Decode(this.senderPublicKey),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.amount),
                Longs.toByteArray(this.fee),
                StringUtil.base58Decode(this.recipient),
                Ints.toByteArray(attachment.length()),
                binaryAttachment
        );
    }
}
