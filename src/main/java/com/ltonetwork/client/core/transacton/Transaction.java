package com.ltonetwork.client.core.transacton;

import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.core.Address;
import com.ltonetwork.client.core.Key;
import com.ltonetwork.client.utils.Encoder;
import com.ltonetwork.client.utils.JsonObject;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Transaction {
    protected int height;
    protected int type;
    protected int version;
    protected long fee;
    protected long timestamp;
    protected String id;
    protected Address sender;
    protected Key senderPublicKey;
    protected ArrayList<byte[]> proofs;

    public Transaction(int type, int version, long fee) {
        this.type = type;
        this.version = version;
        this.fee = fee;
    }

    public Transaction(JsonObject json) {
        if (json.get("height") != null) this.height = (int) json.get("height");
        this.type = (int) json.get("type");
        this.version = (int) json.get("version");
        this.fee = (long) json.get("fee");
        this.timestamp = (long) json.get("timestamp");
        if (json.get("id") != null) this.id = (String) json.get("id");
        this.sender = new Address(json.get("sender").toString().getBytes(StandardCharsets.UTF_8));
        this.senderPublicKey = new Key((String) json.get("senderPublicKey"), Encoder.Encoding.BASE58);
        this.proofs = fetchProofs(new JsonObject((String) json.get("proofs"), true));
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSender(Address sender) {
        this.sender = sender;
    }

    public void setSenderPublicKey(Key senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public void signWith(Account account) {
        if (this.sender == null) {
            setSender(account.getAddressStruct());
            setSenderPublicKey(account.getPublicSignKey());
        }

        if (this.timestamp == 0) {
            setTimestamp(Instant.now().toEpochMilli() * 1000);
        }
    }

    abstract public byte[] toBinary();

    public boolean isSigned() {
        return !(sender == null);
    }

    public byte getNetwork() {
        return this.sender.getChainId();
    }

    private ArrayList<byte[]> fetchProofs(JsonObject jsonProofs) {
        ArrayList<byte[]> proofs = new ArrayList<>();
        Iterator<?> it = jsonProofs.keys();

        while (it.hasNext()) {
            JsonObject curr = new JsonObject(it.next().toString());
            proofs.add(curr.toString().getBytes(StandardCharsets.UTF_8));
        }

        return proofs;
    }
}
