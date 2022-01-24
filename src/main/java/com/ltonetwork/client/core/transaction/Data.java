package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Data extends Transaction {
    private final static long BASE_FEE = 100_000_000;
    private final static long DATA_FEE = 10_000_000;
    private final static byte TYPE = 12;
    private final static List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 3);
    private final DataEntry<?>[] data;

    public Data(DataEntry<?>[] data) {
        super(TYPE, (byte) 3, BASE_FEE);

        checkVersion(version, SUPPORTED_VERSIONS);

        this.data = data;
        updateFeeBasedOnEntries(data);
    }

    public Data(JsonObject json) {
        super(json);

        checkVersion(version, SUPPORTED_VERSIONS);

        JsonObject jsonData = new JsonObject(json.get("data").toString(), true);
        ArrayList<DataEntry<?>> dataFromJson = new ArrayList<>();

        for (int i = 0; i < jsonData.length(); i++) {
            JsonObject curr = new JsonObject(jsonData.get(i), false);
            String key = curr.get("key").toString();
            DataEntry.DataEntryType type = DataEntry.DataEntryType.valueOf(curr.get("type").toString().toUpperCase());
            switch (type) {
                case INTEGER:
                    dataFromJson.add(new DataEntry<>(type, key, Long.parseLong(curr.get("value").toString())));
                    break;
                case BOOLEAN:
                    dataFromJson.add(new DataEntry<>(type, key, Boolean.parseBoolean(curr.get("value").toString())));
                    break;
                case BINARY:
                    dataFromJson.add(new DataEntry<>(type, key, parseBytes(curr.get("value").toString())));
                    break;
                case STRING:
                    dataFromJson.add(new DataEntry<>(type, key, curr.get("value").toString()));
                    break;
            }
        }

        this.data = new DataEntry[dataFromJson.size()];
        for (int i = 0; i < data.length; i++) this.data[i] = dataFromJson.get(i);

        updateFeeBasedOnEntries(data);
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) throw new BadMethodCallException("Sender public key not set");
        if (this.timestamp == 0) throw new BadMethodCallException("Timestamp not set");

        switch(version) {
            case (byte) 3: return toBinaryV3();
            default: throw new IllegalArgumentException("Unknown version " + version);
        }
    }

    private byte[] toBinaryV3() {
        return Bytes.concat(
                new byte[]{this.type},                      // 1b
                new byte[]{this.version},                   // 1b
                new byte[]{this.sender.getChainId()},       // 1b
                Longs.toByteArray(this.timestamp),          // 8b
                this.senderPublicKey.toBinary(),            // 33b/34b
                Longs.toByteArray(this.fee),                // 8b
                Shorts.toByteArray((short) data.length),    // 2b
                getDataEntries()                            // nb
        );
    }

    private byte[] getDataEntries() {
        byte[] ret = new byte[0];
        for (DataEntry<?> entry : data) ret = Bytes.concat(ret, entry.toBinary());
        return ret;
    }

    private void updateFeeBasedOnEntries(DataEntry<?>[] data) {
        byte[] dataBytes = new byte[0];
        for (DataEntry<?> entry : data) dataBytes = Bytes.concat(dataBytes, entry.toBinary());
        if (dataBytes.length > 0) this.fee += (dataBytes.length / (1024 * 256) + 1) * DATA_FEE;
    }

    private byte[] parseBytes(String bytesString) {
        String[] byteValues = bytesString.substring(1, bytesString.length() - 1).split((","));
        byte[] ret = new byte[byteValues.length];
        for (int i = 0; i < ret.length; i++) ret[i] = Byte.parseByte(byteValues[i].trim());
        return ret;
    }
}
