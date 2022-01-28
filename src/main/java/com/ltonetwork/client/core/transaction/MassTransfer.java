package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.Encoder;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MassTransfer extends Transaction {
    private final static long BASE_FEE = 100_000_000;
    private final static long ITEM_FEE = 10_000_000;
    private final static byte TYPE = 11;
    private final static List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 1, (byte) 3);
    private final ArrayList<TransferShort> transfers;
    private String attachment;

    public MassTransfer(byte version) {
        super(TYPE, version, BASE_FEE);

        checkVersion(SUPPORTED_VERSIONS);

        transfers = new ArrayList<>();
        attachment = "";
    }

    public MassTransfer() {
        this((byte) 3);
    }

    public MassTransfer(JsonObject json) {
        super(json);

        checkVersion(SUPPORTED_VERSIONS);

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
        this.attachment = (json.has("attachment")) ? json.get("attachment").toString() : "";
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
        checkToBinary();

        switch (version) {
            case (byte) 1:
                return toBinaryV1();
            case (byte) 3:
                return toBinaryV3();
            default:
                throw new IllegalArgumentException("Unknown version " + version);
        }
    }

    public JsonObject toJson() {
        JsonObject json = super.toJson();

        JSONArray transfersJsonArray = new JSONArray();
        for (TransferShort transfer : transfers) {
            try {
                transfersJsonArray.put(transfer.toJson().getObject());
            } catch (JSONException e) {
                throw new IllegalArgumentException("Unable to parse transfer entry");
            }
        }

        json.put("transfers", transfersJsonArray);
        if (!attachment.equals("")) json.put("attachment", attachment);

        return json;
    }

    private byte[] toBinaryV1() {
        System.out.println();
        return Bytes.concat(
                new byte[]{this.type},                              // 1b
                new byte[]{this.version},                           // 1b
                this.senderPublicKey.getRaw(),                      // 32b
                Shorts.toByteArray((short) transfers.size()),       // 2b
                transfersToBinary(),                                // (26b + 8b)*n
                Longs.toByteArray(this.timestamp),                  // 8b
                Longs.toByteArray(this.fee),                        // 8b
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
                Shorts.toByteArray((short) transfers.size()),       // 2b
                transfersToBinary(),                                // (26b + 8b)*n
                Shorts.toByteArray((short) attachment.length()),    // 2b
                Encoder.base58Decode(this.attachment)               // mb
        );
    }

    private byte[] transfersToBinary() {
        ArrayList<Byte> transfersBytes = new ArrayList<>();

        for (TransferShort transfer : transfers) {
            for (Byte rec : Encoder.base58Decode(transfer.getRecipient().getAddress())) {
                transfersBytes.add(rec);
            }
            for (Byte am : Longs.toByteArray(transfer.getAmount())) {
                transfersBytes.add(am);
            }
        }

        return Bytes.toArray(transfersBytes);
    }
}
