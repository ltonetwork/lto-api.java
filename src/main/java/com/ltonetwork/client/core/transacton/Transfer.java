package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.Encoder;

public class Transfer extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static byte TYPE = 4;
    private final static byte VERSION = 2;
    private final long amount;
    private String attachment;
    private final Address recipient;

    public Transfer(int amount, Address recipient) {
        super(TYPE, VERSION, MINIMUM_FEE);

        if (amount <= 0) {
            throw new InvalidArgumentException("Invalid amount; should be greater than 0");
        }

        this.amount = amount;
        this.recipient = recipient;
    }

    public Transfer(JsonObject json) {
        super(json);
        this.amount = Long.parseLong(json.get("amount").toString());
        this.recipient = new Address(json.get("recipient").toString());
    }

    public void setAttachment(String message, Encoding encoding) {
        this.attachment = Encoder.base58Encode(Encoder.decode(message, encoding));
    }

    public void setAttachment(String message) {
        Encoder.isBase58Encoded(message);
        setAttachment(message, Encoding.BASE58);
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) {
            throw new BadMethodCallException("Sender public key not set");
        }

        if (this.timestamp == 0) {
            throw new BadMethodCallException("Timestamp not set");
        }

        byte[] binaryAttachment = Bytes.concat(
                new byte[]{this.type},
                new byte[]{this.version},
                this.senderPublicKey.getRaw(),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.amount),
                Longs.toByteArray(this.fee),
                Encoder.base58Decode(this.recipient.getAddress()));

        if (attachment != null) {
            binaryAttachment = Bytes.concat(
                    binaryAttachment,
                    Shorts.toByteArray((short) attachment.length()),
                    Encoder.base58Decode(this.attachment)
            );
        }

        return binaryAttachment;
    }
}
