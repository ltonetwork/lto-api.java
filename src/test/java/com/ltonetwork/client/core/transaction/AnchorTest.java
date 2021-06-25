package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.TestUtil;
import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.core.transacton.Anchor;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;


public class AnchorTest {
    byte chainId;
    Anchor tx;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void init() {
        chainId = 84;
        tx = new Anchor("8A8TXZioKiCpKBt7dYx8yAEwnWGhp", Encoding.BASE58);
    }

    @Test
    public void testToBinary() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        assertEquals(101, tx.toBinary().length);
    }

    @Test
    public void testToBinaryFail() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Sender public key not set");

        tx.toBinary();
    }

    @Test
    public void testGetHashes() {
        assertEquals("8A8TXZioKiCpKBt7dYx8yAEwnWGhp", tx.getHashes(Encoding.BASE58)[0]);
    }

    @Test
    public void testGetHash() {
        assertEquals("8A8TXZioKiCpKBt7dYx8yAEwnWGhp", tx.getHash(Encoding.BASE58));
    }

    @Test
    public void testGetHashFail() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Method 'getHash' can't be used on a multi-anchor tx");

        tx.addHash("test", Encoding.RAW);
        assertEquals("8A8TXZioKiCpKBt7dYx8yAEwnWGhk", tx.getHash(Encoding.BASE58));
    }

    @Test
    public void testAddHash() {
        tx.addHash("test", Encoding.RAW);
        assertEquals("test", tx.getHashes(Encoding.RAW)[1]);
    }

    @Test
    public void testCreateWithJsonSingle() {
        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 8,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 2,\n" +
                        "  \"anchors\": [\"3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx\"],\n" +
                        "  \"height\": 22654\n" +
                        "}", false);

        Anchor jsonTx = new Anchor(json);
        assertEquals(106, jsonTx.toBinary().length);
    }
}
