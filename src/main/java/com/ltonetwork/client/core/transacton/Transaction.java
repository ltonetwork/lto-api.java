package com.ltonetwork.client.core.transacton;

import java.time.Instant;
import java.util.ArrayList;

import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.core.Address;

public abstract class Transaction {
    protected int height;
    protected final int type;
    protected final int version;
    protected long fee;
    protected long timestamp;
    protected String id;
    protected Address sender;
    protected String senderPublicKey;
    protected ArrayList<byte[]> proofs;

    public Transaction(int type, int version, long fee) {
        this.type = type;
        this.version = version;
        this.fee = fee;
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

    public void setSenderPublicKey(String senderPublicKey) {
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

    public byte getNetwork() {
        return this.sender.getChainId();
    }
}
