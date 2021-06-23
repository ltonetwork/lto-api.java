package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.core.transacton.Transfer;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.Key;
import com.ltonetwork.client.types.KeyPair;
import com.ltonetwork.client.utils.Encoder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;


public class TransferTest {
    byte chainId;
    Transfer tx;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void init() {
        chainId = 84;
        tx = new Transfer(1, new Address("3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx", chainId));
    }

    @Test
    public void testSignWith() {
        Account account = createAccount();

        assertFalse(tx.isSigned());
        tx.signWith(account);
        assertTrue(tx.isSigned());
    }

    @Test
    public void testToBinaryNoAttachment() {
        Account account = createAccount();
        tx.signWith(account);

        assertEquals(110, tx.toBinary().length);
    }

    @Test
    public void testToBinary() {
        Account account = createAccount();
        tx.signWith(account);
        tx.setAttachment("test");

        assertEquals(119, tx.toBinary().length);
    }

    @Test
    public void testToBinaryFail() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Sender public key not set");

        tx.toBinary();
    }

    @Test
    public void testGetNetwork() {
        Account account = createAccount();
        tx.signWith(account);

        byte exp = 84;
        assertEquals(exp, tx.getNetwork());
    }

    private Account createAccount() {
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
