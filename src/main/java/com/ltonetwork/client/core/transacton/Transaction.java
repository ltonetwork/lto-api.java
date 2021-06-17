package com.ltonetwork.client.core.transacton;

import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Key;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.Signature;
import com.ltonetwork.client.utils.JsonObject;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Transaction {
    protected int height;
    protected byte type;
    protected byte version;
    protected long fee;
    protected long timestamp;
    protected TransactionId id;
    protected Address sender;
    protected Key senderPublicKey;
    protected ArrayList<Signature> proofs;

    public Transaction(byte type, byte version, long fee) {
        this.type = type;
        this.version = version;
        this.fee = fee;
    }

    public Transaction(JsonObject json) {
        if (json.get("height") != null) this.height = (int) json.get("height");
        this.type = (byte) json.get("type");
        this.version = (byte) json.get("version");
        this.fee = (long) json.get("fee");
        this.timestamp = (long) json.get("timestamp");
        if (json.get("id") != null) this.id = new TransactionId((String) json.get("id"));
        this.sender = new Address(json.get("sender").toString());
        this.senderPublicKey = new Key((String) json.get("senderPublicKey"), Encoding.BASE58);
        this.proofs = fetchProofs(new JsonObject((String) json.get("proofs"), true));
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

        this.proofs.add(new Signature(this.toBinary(), account.getSign().getSecretkey()));
    }

    abstract public byte[] toBinary();

    public boolean isSigned() {
        return !(sender == null);
    }

    public byte getNetwork() {
        return this.sender.getChainId();
    }

    private ArrayList<Signature> fetchProofs(JsonObject jsonProofs) {
        ArrayList<Signature> proofs = new ArrayList<>();
        Iterator<?> it = jsonProofs.keys();

        while (it.hasNext()) {
            JsonObject curr = new JsonObject(it.next().toString());
            proofs.add(new Signature(curr.toString().getBytes(StandardCharsets.UTF_8)));
        }

        return proofs;
    }
}
