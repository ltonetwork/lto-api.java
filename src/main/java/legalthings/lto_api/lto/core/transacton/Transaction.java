package legalthings.lto_api.lto.core.transacton;

import java.time.Instant;
import java.util.ArrayList;

import legalthings.lto_api.lto.core.Account;

public abstract class Transaction {
    protected int height;
    protected final int type;
    protected final int version;
    protected final long fee;
    protected long timestamp;
    protected String id;
    protected String sender;
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

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public void signWith(Account account) {
        if (this.sender == null) {
            setSender(account.getAddress());
            setSenderPublicKey(account.getPublicSignKey());
        }

        if (this.timestamp == 0) {
            setTimestamp(Instant.now().toEpochMilli() * 1000);
        }

        proofs.add(account.signBytes(toBinary()));
    }

    abstract public byte[] toBinary();

//    TODO: SHOULD GET PART OF THE KEY?
    public byte[] getNetwork() {
        return this.senderPublicKey.getBytes();
    }
}
