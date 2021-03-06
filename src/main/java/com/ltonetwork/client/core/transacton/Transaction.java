package com.ltonetwork.client.core.transacton;

import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.types.*;

import java.time.Instant;
import java.util.ArrayList;

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
        this.proofs = new ArrayList<>();
    }

    public Transaction(JsonObject json) {
        if (json.has("height")) this.height = Integer.parseInt(json.get("height").toString());
        this.type = Byte.parseByte(json.get("type").toString());
        this.version = Byte.parseByte(json.get("version").toString());
        this.fee = Long.parseLong(json.get("fee").toString());
        this.timestamp = Long.parseLong(json.get("timestamp").toString());
        if (json.has("id")) this.id = new TransactionId(json.get("id").toString());
        if (json.has("chainId")) {
            this.sender = new Address(json.get("sender").toString(), Byte.parseByte(json.get("chainId").toString()));
        } else {
            this.sender = new Address(json.get("sender").toString());
        }
        this.senderPublicKey = new Key(json.get("senderPublicKey").toString(), Encoding.BASE58);
        if (json.has("proofs")) this.proofs = fetchProofs(new JsonObject(json.get("proofs").toString(), true));
    }

    public void signWith(Account account) {
        if (this.sender == null) {
            this.sender = account.getAddressStruct();
            this.senderPublicKey = account.getPublicSignKey();
        }

        if (this.timestamp == 0) {
            this.timestamp = Instant.now().toEpochMilli() * 1000;
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

        for (int i = 0; i < jsonProofs.length(); i++) {
            proofs.add(new Signature(jsonProofs.get(i), Encoding.BASE58));
        }

        return proofs;
    }
}
