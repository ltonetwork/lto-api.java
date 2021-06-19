package com.ltonetwork.client.core.transacton;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.utils.CryptoUtil;
import com.ltonetwork.client.utils.Encoder;
import com.ltonetwork.client.types.JsonObject;

import java.nio.charset.StandardCharsets;

public class CancelSponsor extends Transaction {
    private final static long MINIMUM_FEE = 500_000_000;
    private final static byte TYPE = 19;
    private final static byte VERSION = 1;
    private final String recipient;

    public CancelSponsor(String recipient) {
        super(TYPE, VERSION, MINIMUM_FEE);

        if (!CryptoUtil.isValidAddress(recipient, Encoding.BASE58)) {
            throw new InvalidArgumentException("Invalid recipient address; is it base58 encoded?");
        }

        this.recipient = recipient;
    }

    public CancelSponsor(JsonObject json) {
        super(json);
        this.recipient = (String) json.get("recipient");
    }

    public byte[] toBinary() {
        if (this.senderPublicKey == null) {
            throw new BadMethodCallException("Sender public key not set");
        }

        if (this.timestamp == 0) {
            throw new BadMethodCallException("Timestamp not set");
        }

        return Bytes.concat(
                Longs.toByteArray(this.type),
                Longs.toByteArray(this.version),
                new byte[this.getNetwork()],
                this.senderPublicKey.toBase58().getBytes(StandardCharsets.UTF_8),
                Encoder.base58Decode(this.recipient),
                Longs.toByteArray(this.timestamp),
                Longs.toByteArray(this.fee)
        );
    }
}
