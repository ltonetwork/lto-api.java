package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.utils.Encoder;
import com.ltonetwork.client.utils.JsonObject;

import java.nio.charset.StandardCharsets;

public class SetScript extends Transaction {
    private final static long MINIMUM_FEE = 500_000_000;
    private final static int TYPE = 13;
    private final static int VERSION = 1;
    private final String script;
    private int complexity;
    private long extraFee;

    public SetScript(String script) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.script = (script == null) ? null : script.replaceAll("^(base64:)?", "base64:");
    }

    public SetScript(JsonObject json) {
        super(json);
        String script = (String) json.get("script");
        if (json.get("complexity") != null) this.complexity = (Integer) json.get("complexity");
        if (json.get("extraFee") != null) this.extraFee = (Integer) json.get("extraFee");
        this.script = (script == null) ? null : script.replaceAll("^(base64:)?", "base64:");
    }

    public long getEstimatedFee() {
        if(extraFee == 0) throw new BadMethodCallException("Can't estimate fee; the script hasn't been compiled");
        return MINIMUM_FEE + extraFee;
    }

    public int getComplexity() {
        if(complexity == 0) throw new BadMethodCallException("Can't fetch complexity; the script hasn't been compiled");
        return complexity;
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) {
            throw new BadMethodCallException("Sender public key not set");
        }

        if (this.timestamp == 0) {
            throw new BadMethodCallException("Timestamp not set");
        }

        byte[] binaryScript = Encoder.base64Decode(this.script.replaceAll("^(base64:)?", ""));

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                new byte[this.getNetwork()],
                this.senderPublicKey.toBase58().getBytes(StandardCharsets.UTF_8),
                Ints.toByteArray(binaryScript.length),
                binaryScript,
                Longs.toByteArray(this.fee),
                Longs.toByteArray(this.timestamp)
        );
    }
}
