package com.ltonetwork.client.core.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.Encoder;

import java.util.Arrays;
import java.util.List;

public class SetScript extends Transaction {
    private final static long MINIMUM_FEE = 500_000_000;
    private final static byte TYPE = 13;
    private final static List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 1, (byte) 3);
    private final String script;
    private int complexity;
    private long extraFee;

    public SetScript(String script, byte version) {
        super(TYPE, version, MINIMUM_FEE);

        checkVersion(SUPPORTED_VERSIONS);

        this.script = (script == null) ? null : script.replaceAll("^(base64:)?", "base64:");
    }

    public SetScript(String script) {
        this(script, (byte) 3);
    }

    public SetScript(JsonObject json) {
        super(json);

        checkVersion(SUPPORTED_VERSIONS);

        String script = json.get("script").toString();

        if (json.has("complexity")) this.complexity = Integer.parseInt(json.get("complexity").toString());
        if (json.has("extraFee")) this.extraFee = Integer.parseInt(json.get("extraFee").toString());

        this.script = (script == null) ? null : script.replaceAll("^(base64:)?", "base64:");
    }

    public long getEstimatedFee() {
        if (extraFee == 0) throw new BadMethodCallException("Can't estimate fee; the script hasn't been compiled");
        return MINIMUM_FEE + extraFee;
    }

    public int getComplexity() {
        if (complexity == 0)
            throw new BadMethodCallException("Can't fetch complexity; the script hasn't been compiled");
        return complexity;
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

    private byte[] toBinaryV1() {
        byte[] binaryScript = scriptToBinary();

        return Bytes.concat(
                new byte[]{this.type},                              // 1b
                new byte[]{this.version},                           // 1b
                new byte[]{this.getNetwork()},                      // 1b
                this.senderPublicKey.getRaw(),                      // 32b
                new byte[]{(byte) 1},                               // 1b
                Shorts.toByteArray((short) binaryScript.length),    // 2b
                binaryScript,                                       // nb
                Longs.toByteArray(this.fee),                        // 8b
                Longs.toByteArray(this.timestamp)                   // 8b
        );
    }

    private byte[] toBinaryV3() {
        byte[] binaryScript = scriptToBinary();

        return Bytes.concat(
                new byte[]{this.type},                              // 1b
                new byte[]{this.version},                           // 1b
                new byte[]{this.getNetwork()},                      // 1b
                Longs.toByteArray(this.timestamp),                  // 8b
                this.senderPublicKey.toBinary(),                    // 33b|34b
                Longs.toByteArray(this.fee),                        // 8b
                Shorts.toByteArray((short) binaryScript.length),    // 2b
                binaryScript                                        // nb
        );
    }

    private byte[] scriptToBinary() {
        return Encoder.base64Decode(this.script.replaceAll("^(base64:)?", ""));
    }
}
