package com.ltonetwork.client;

import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.types.*;
import com.ltonetwork.client.utils.Encoder;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class TestUtil {
    public static Account createAccount() {
        KeyPair sign = new KeyPair(
                new PublicKey(Encoder.base58Decode("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y"), Encoding.RAW),
                new PrivateKey(Encoder.base58Decode("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp"), Encoding.RAW)
        );

        KeyPair encrypt = new KeyPair(
                new PublicKey(Encoder.base58Decode("BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN"), Encoding.RAW),
                new PrivateKey(Encoder.base58Decode("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6"), Encoding.RAW)
        );

        Address address = new Address("3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy");

        return new Account(address, encrypt, sign);
    }
}
