package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.CryptoUtil;
import com.ltonetwork.client.utils.Encoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class MassTransfer extends Transaction {
    private final static long BASE_FEE = 100_000_000;
    private final static long ITEM_FEE = 10_000_000;
    private final static byte TYPE = 11;
    private final static byte VERSION = 1;
    private final ArrayList<TransferShort> transfers;
    private String attachment = "";

    public MassTransfer() {
        super(TYPE, VERSION, BASE_FEE);
        transfers = new ArrayList<>();
    }

    public MassTransfer(JsonObject json) {
        super(json);

        JsonObject jsonTransfers = new JsonObject((String) json.get("transfers"), true);
        ArrayList<TransferShort> transfers = new ArrayList<>();
        Iterator<?> it = jsonTransfers.keys();

        while (it.hasNext()) {
            JsonObject curr = new JsonObject(it.next().toString());
            transfers.add(new TransferShort(
                    new Address((String) curr.get("recipient")),
                    (long) curr.get("amount")
            ));
        }

        this.transfers = transfers;
    }

    public void setAttachment(String message, Encoding encoding) {
        this.attachment = Encoder.base58Encode(Encoder.decode(message, encoding));
    }

    public void setAttachment(String message) {
        setAttachment(message, Encoding.RAW);
    }

    public void addTransfer(String recipient, int amount) {

        if (amount <= 0) {
            throw new InvalidArgumentException("Invalid amount; should be greater than 0");
        }

        if (!CryptoUtil.isValidAddress(recipient, Encoding.BASE58)) {
            throw new InvalidArgumentException("Invalid recipient address; is it base58 encoded?");
        }

        transfers.add(new TransferShort(new Address(recipient, sender.getChainId()), amount));
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
            for (Byte rec : Encoder.base58Decode(transfer.getRecipient().getAddress())) {
                transfersBytes.add(rec);
            }
            for (Byte am : Longs.toByteArray(transfer.getAmount())) {
                transfersBytes.add(am);
            }
        }

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                this.senderPublicKey.toBase58().getBytes(StandardCharsets.UTF_8),
                Ints.toByteArray(transfers.size()),
                Bytes.toArray(transfersBytes),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee),
                Ints.toByteArray(attachment.length()),
                binaryAttachment
        );
    }
}
