package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.utils.Encoder;

public class SetScript extends Transaction {
    private final static long MINIMUM_FEE = 500_000_000;
    private final static int TYPE = 13;
    private final static int VERSION = 1;
    private final String script;

    public SetScript(String script) {
        super(TYPE, VERSION, MINIMUM_FEE);
        this.script = (script == null) ? null : script.replaceAll("^(base64:)?", "base64:");
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
                Encoder.base58Decode(this.senderPublicKey),
                Ints.toByteArray(binaryScript.length),
                binaryScript,
                Longs.toByteArray(this.fee),
                Longs.toByteArray(this.timestamp)
        );
    }
}
