package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.types.PublicKey;
import com.ltonetwork.client.utils.CryptoUtil;
import com.ltonetwork.seasalt.Binary;
import com.ltonetwork.client.types.Key.KeyType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class Transaction {
    protected int height;
    protected byte type;
    protected byte version;
    protected long fee;
    protected long timestamp;
    protected TransactionId id;
    protected Address sender;
    protected PublicKey senderPublicKey;
    protected ArrayList<com.ltonetwork.seasalt.sign.Signature> proofs;
    protected Account sponsor;

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
        this.sender = new Address(json.get("sender").toString());
        this.senderPublicKey = new PublicKey(json.get("senderPublicKey").toString(), Encoding.BASE58,
        KeyType.(json.get("senderKeyType").toString()));
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

        this.proofs.add(CryptoUtil.signDetached(this.toBinary(), account.getSign().getPrivateKey()));
    }

    public void sponsorWith(Account account) {
        if (!isSigned())
            throw new BadMethodCallException("Transaction should be signed by the sender before adding a sponsor");

        signWith(account);
        this.sponsor = account;
    }

    abstract public byte[] toBinary();

    public boolean isSigned() {
        return !(sender == null);
    }

    public byte getNetwork() {
        return this.sender.getChainId();
    }

    public Account getSponsor() {
        return this.sponsor;
    }

    public ArrayList<com.ltonetwork.seasalt.sign.Signature> getProofs() {
        return this.proofs;
    }

    protected void checkToBinary() {
        if (this.senderPublicKey == null) throw new BadMethodCallException("Sender public key not set");
        if (this.timestamp == 0) throw new BadMethodCallException("Timestamp not set");
    }

    protected void checkVersion(List<Byte> supportedVersions) {
        if (!supportedVersions.contains(version))
            throw new IllegalArgumentException("Unknown version " + version + ", supported versions are: " + supportedVersions);
    }

    private ArrayList<com.ltonetwork.seasalt.sign.Signature> fetchProofs(JsonObject jsonProofs) {
        ArrayList<com.ltonetwork.seasalt.sign.Signature> proofs = new ArrayList<>();

        for (int i = 0; i < jsonProofs.length(); i++)
            proofs.add(new com.ltonetwork.seasalt.sign.Signature(Binary.fromBase58(jsonProofs.get(i)).getBytes()));

        return proofs;
    }
}
