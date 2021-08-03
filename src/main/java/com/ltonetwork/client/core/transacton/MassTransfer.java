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

import java.util.ArrayList;

public class MassTransfer extends Transaction {
    private final static long BASE_FEE = 100_000_000;
    private final static long ITEM_FEE = 10_000_000;
    private final static byte TYPE = 11;
    private final static byte VERSION = 1;
    private final ArrayList<TransferShort> transfers;
    private String attachment;

    public MassTransfer() {
        super(TYPE, VERSION, BASE_FEE);
        transfers = new ArrayList<>();
    }

    public MassTransfer(JsonObject json) {
        super(json);

        JsonObject jsonTransfers = new JsonObject(json.get("transfers").toString(), true);
        ArrayList<TransferShort> transfers = new ArrayList<>();

        for (int i = 0; i < jsonTransfers.length(); i++) {
            JsonObject curr = new JsonObject(jsonTransfers.get(i), false);
            transfers.add(new TransferShort(
                    new Address(curr.get("recipient").toString()),
                    Long.parseLong(curr.get("amount").toString())
            ));
        }

        this.transfers = transfers;
        if (json.has("attachment")) this.attachment = json.get("attachment").toString();
    }

    public void setAttachment(String message, Encoding encoding) {
        this.attachment = Encoder.base58Encode(Encoder.decode(message, encoding));
    }

    public void setAttachment(String message) {
        setAttachment(message, Encoding.RAW);
    }

    public void addTransfer(Address recipient, int amount) {

        if (amount <= 0) {
            throw new InvalidArgumentException("Invalid amount; should be greater than 0");
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

        byte[] ret = Bytes.concat(
                new byte[]{this.type},
                new byte[]{this.version},
                this.senderPublicKey.toRaw(),
                Shorts.toByteArray((short) transfers.size())
        );

        ArrayList<Byte> transfersBytes = new ArrayList<>();

        for (TransferShort transfer : transfers) {
            for (Byte rec : Encoder.base58Decode(transfer.getRecipient().getAddress())) {
                transfersBytes.add(rec);
            }
            for (Byte am : Longs.toByteArray(transfer.getAmount())) {
                transfersBytes.add(am);
            }
        }

        ret = Bytes.concat(
                ret,
                Bytes.toArray(transfersBytes),
                Longs.toByteArray(this.fee),
                Longs.toByteArray(this.timestamp)
        );

        if (attachment != null) {
            ret = Bytes.concat(
                    ret,
                    Shorts.toByteArray((short) attachment.length()),
                    Encoder.base58Decode(this.attachment)
            );
        }

        return ret;
    }
}
