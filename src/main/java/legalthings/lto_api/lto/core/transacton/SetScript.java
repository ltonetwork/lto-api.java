package legalthings.lto_api.lto.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.lto.exceptions.InvalidArgumentException;
import legalthings.lto_api.utils.main.StringUtil;

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

        byte[] binaryScript = StringUtil.base64Decode(this.script.replaceAll("^(base64:)?", ""));

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                this.getNetwork(),
                StringUtil.base58Decode(this.senderPublicKey),
                Ints.toByteArray(binaryScript.length),
                binaryScript,
                Longs.toByteArray(this.fee),
                Longs.toByteArray(this.timestamp)
        );
    }
}
