package legalthings.lto_api.lto.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.lto.exceptions.InvalidArgumentException;
import legalthings.lto_api.utils.main.CryptoUtil;
import legalthings.lto_api.utils.main.Encoder;

import java.util.ArrayList;

public class MassTransfer extends Transaction {
    private final static long BASE_FEE = 100_000_000;
    private final static long ITEM_FEE = 10_000_000;
    private final static int TYPE = 11;
    private final static int VERSION = 1;
    private final ArrayList<TransferShort> transfers;
    private String attachment = "";

    public MassTransfer(int amount, String recipient) {
        super(TYPE, VERSION, BASE_FEE);
        transfers = new ArrayList<>();
    }

    public void setAttachment(String message, String encoding) {
        this.attachment = Encoder.fromXStringToBase58String(message, encoding);
    }

    public void setAttachment(String message) {
        setAttachment(message, "raw");
    }

    public void addTransfer(String recipient, int amount) {

        if (amount <= 0) {
            throw new InvalidArgumentException("Invalid amount; should be greater than 0");
        }

        if (!CryptoUtil.isValidAddress(recipient, "base58")) {
            throw new InvalidArgumentException("Invalid recipient address; is it base58 encoded?");
        }

        transfers.add(new TransferShort(recipient, amount));
        this.fee += ITEM_FEE;
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) {
            throw new BadMethodCallException("Sender public key not set");
        }

        if (this.timestamp == 0) {
            throw new BadMethodCallException("Timestamp not set");
        }

        byte[] binaryAttachment = Encoder.base58Decode(this.attachment);

        ArrayList<Byte> transfersBytes = new ArrayList<>();

        for (TransferShort transfer : transfers) {
            for (Byte rec : Encoder.base58Decode(transfer.getRecipient())) {
                transfersBytes.add(rec);
            }
            for (Byte am : Ints.toByteArray(transfer.getAmount())) {
                transfersBytes.add(am);
            }
        }

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                Encoder.base58Decode(this.senderPublicKey),
                Ints.toByteArray(transfers.size()),
                Bytes.toArray(transfersBytes),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee),
                Ints.toByteArray(attachment.length()),
                binaryAttachment
        );
    }
}
