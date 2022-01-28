package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.types.Key;
import com.ltonetwork.client.types.PublicKey;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Register extends Transaction {
    private final static long BASE_FEE = 100_000_000;
    private final static long VAR_FEE = 10_000_000;
    private final static byte TYPE = 20;
    private final static List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 3);
    private final ArrayList<PublicKey> accounts;

    public Register(byte version) {
        super(TYPE, version, BASE_FEE);

        checkVersion(SUPPORTED_VERSIONS);

        accounts = new ArrayList<>();
    }

    public Register() {
        this((byte) 3);
    }

    public Register(JsonObject json) {
        super(json);

        checkVersion(SUPPORTED_VERSIONS);

        JsonObject jsonAccounts = new JsonObject(json.get("accounts").toString(), true);
        accounts = new ArrayList<>();

        for (int i = 0; i < jsonAccounts.length(); i++) {
            JsonObject curr = new JsonObject(jsonAccounts.get(i), false);
            Key.KeyType keyType = Key.KeyType.valueOf(curr.get("keyType").toString().toUpperCase(Locale.ROOT));
            String key = curr.get("publicKey").toString();
            accounts.add(new PublicKey(key, Encoding.BASE58, keyType));
            this.fee += VAR_FEE;
        }
    }

    public void addAccount(PublicKey account) {
        accounts.add(account);
        this.fee += VAR_FEE;
    }

    public byte[] toBinary() {
        checkToBinary();

        switch (version) {
            case (byte) 3:
                return toBinaryV3();
            default:
                throw new IllegalArgumentException("Unknown version " + version);
        }
    }

    public JsonObject toJson() {
        JsonObject json = super.toJson();

        JSONArray keysJsonArray = new JSONArray();
        for (PublicKey key : accounts) {
            try {
                keysJsonArray.put(key.toJson().getObject());
            } catch (JSONException e) {
                throw new IllegalArgumentException("Unable to parse publickey " + key.getBase58() + " to JSON");
            }
        }

        json.put("data", keysJsonArray);

        return json;
    }

    private byte[] toBinaryV3() {
        return Bytes.concat(
                new byte[]{this.type},                          // 1b
                new byte[]{this.version},                       // 1b
                new byte[]{this.getNetwork()},                  // 1b
                Longs.toByteArray(this.timestamp),              // 8b
                this.senderPublicKey.toBinary(),                // 33b|34b
                Longs.toByteArray(this.fee),                    // 8b
                Shorts.toByteArray((short) accounts.size()),    // 2b
                keysToBinary()                                  // (33b|34b)*n
        );
    }

    private byte[] keysToBinary() {
        byte[] binaryKeys = new byte[0];
        for (PublicKey key : accounts) binaryKeys = Bytes.concat(binaryKeys, key.toBinary());
        return binaryKeys;
    }
}
