package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.utils.CryptoUtil;
import com.ltonetwork.client.utils.Encoder;
import com.ltonetwork.client.utils.JsonObject;

import java.nio.charset.StandardCharsets;

public class Transfer extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static int TYPE = 4;
    private final static int VERSION = 2;
    private long amount;
    private String attachment = "";
    private final String recipient;

    public Transfer(int amount, String recipient) {
        super(TYPE, VERSION, MINIMUM_FEE);

        if (amount <= 0) {
            throw new InvalidArgumentException("Invalid amount; should be greater than 0");
        }

        if (!CryptoUtil.isValidAddress(recipient, Encoding.BASE58)) {
            throw new InvalidArgumentException("Invalid recipient address; is it base58 encoded?");
        }

        this.amount = amount;
        this.recipient = recipient;
    }

    public Transfer(JsonObject json) {
        super(json);
        this.amount = (long) json.get("amount");
        this.recipient = (String) json.get("recipient");
    }

    public void setAttachment(String message, Encoding encoding) {
        this.attachment = Encoder.base58Encode(Encoder.decode(message, encoding));
    }

    public void setAttachment(String message) {
        setAttachment(message, Encoding.RAW);
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
                this.senderPublicKey.toBase58().getBytes(StandardCharsets.UTF_8),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.amount),
                Longs.toByteArray(this.fee),
                Encoder.base58Decode(this.recipient),
                Ints.toByteArray(attachment.length()),
                binaryAttachment
        );
    }
}
