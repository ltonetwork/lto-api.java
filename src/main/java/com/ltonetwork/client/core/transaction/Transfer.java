package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.Encoder;

import java.util.Arrays;
import java.util.List;

public class Transfer extends Transaction {
    private final static long MINIMUM_FEE = 100_000_000;
    private final static byte TYPE = 4;
    private final static List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 1, (byte) 2, (byte) 3);
    private final long amount;
    private final Address recipient;
    private String attachment;

    public Transfer(int amount, Address recipient, byte version) {
        super(TYPE, version, MINIMUM_FEE);

        checkVersion(SUPPORTED_VERSIONS);
        if (amount <= 0) throw new InvalidArgumentException("Invalid amount; should be greater than 0");

        this.amount = amount;
        this.recipient = recipient;
        this.attachment = "";
    }

    public Transfer(int amount, Address recipient) {
        this(amount, recipient, (byte) 3);
    }

    public Transfer(JsonObject json) {
        super(json);
        long am = Long.parseLong(json.get("amount").toString());

        checkVersion(SUPPORTED_VERSIONS);
        if (am <= 0) throw new InvalidArgumentException("Invalid amount; should be greater than 0");

        this.amount = am;
        this.recipient = new Address(json.get("recipient").toString());
        this.attachment = (json.has("attachment")) ? json.get("attachment").toString() : "";
    }

    public void setAttachment(String message, Encoding encoding) {
        this.attachment = Encoder.base58Encode(Encoder.decode(message, encoding));
    }

    public void setAttachment(String message) {
        Encoder.isBase58Encoded(message);
        setAttachment(message, Encoding.BASE58);
    }

    public byte[] toBinary() {
        checkToBinary();

        switch (version) {
            case (byte) 1:
                return toBinaryV1();
            case (byte) 2:
                return toBinaryV2();
            case (byte) 3:
                return toBinaryV3();
            default:
                throw new IllegalArgumentException("Unknown version " + version);
        }
    }

    private byte[] toBinaryV1() {
        return Bytes.concat(
                new byte[]{this.type},                              // 1b
                this.senderPublicKey.getRaw(),                      // 32b
                Longs.toByteArray(this.timestamp),                  // 8b
                Longs.toByteArray(this.amount),                     // 8b
                Longs.toByteArray(this.fee),                        // 8b
                Encoder.base58Decode(this.recipient.getAddress()),  // 26b
                Shorts.toByteArray((short) attachment.length()),    // 2b
                Encoder.base58Decode(this.attachment)               // mb
        );
    }

    private byte[] toBinaryV2() {
        return Bytes.concat(
                new byte[]{this.type},                              // 1b
                new byte[]{this.version},                           // 1b
                this.senderPublicKey.getRaw(),                      // 32b
                Longs.toByteArray(this.timestamp),                  // 8b
                Longs.toByteArray(this.amount),                     // 8b
                Longs.toByteArray(this.fee),                        // 8b
                Encoder.base58Decode(this.recipient.getAddress()),  // 26b
                Shorts.toByteArray((short) attachment.length()),    // 2b
                Encoder.base58Decode(this.attachment)               // mb
        );
    }


    private byte[] toBinaryV3() {
        return Bytes.concat(
                new byte[]{this.type},                              // 1b
                new byte[]{this.version},                           // 1b
                new byte[]{this.getNetwork()},                      // 1b
                Longs.toByteArray(this.timestamp),                  // 8b
                this.senderPublicKey.toBinary(),                    // 33b|34b
                Longs.toByteArray(this.fee),                        // 8b
                Encoder.base58Decode(this.recipient.getAddress()),  // 26b
                Longs.toByteArray(this.amount),                     // 8b
                Shorts.toByteArray((short) attachment.length()),    // 2b
                Encoder.base58Decode(this.attachment)               // mb
        );
    }
}
