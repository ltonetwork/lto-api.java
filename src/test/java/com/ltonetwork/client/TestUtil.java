package com.ltonetwork.client;

import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.Key;
import com.ltonetwork.client.types.KeyPair;
import com.ltonetwork.client.utils.Encoder;

public class TestUtil {
    static byte chainId = 84;

    public static Account createAccount() {
        KeyPair sign = new KeyPair(
                new Key(Encoder.base58Decode("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y"), Encoding.RAW),
                new Key(Encoder.base58Decode("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp"), Encoding.RAW)
        );

        KeyPair encrypt = new KeyPair(
                new Key(Encoder.base58Decode("BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN"), Encoding.RAW),
                new Key(Encoder.base58Decode("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6"), Encoding.RAW)
        );

        Address address = new Address("3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy", chainId);

        return new Account(address, encrypt, sign);
    }
}
