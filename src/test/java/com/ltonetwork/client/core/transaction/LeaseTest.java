package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.TestUtil;
import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.JsonObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;


public class LeaseTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    Lease tx;

    @Before
    public void init() {
        tx = new Lease(1, new Address("3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx"));
    }

    @Test
    public void testToBinaryV3() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        assertEquals(86, tx.toBinary().length);
    }

    @Test
    public void testToBinaryV2() {
        Lease txV2 = new Lease(1, new Address("3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx"), (byte) 2);
        Account account = TestUtil.createAccount();
        txV2.signWith(account);

        assertEquals(85, txV2.toBinary().length);
    }

    @Test
    public void testToBinaryFail() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Sender public key not set");

        tx.toBinary();
    }

    @Test
    public void testCreateWithJson() {
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
                        "  \"recipient\": \"3Mv7ajrPLKewkBNqfxwRZoRwW6fziehp7dQ\",\n" +
                        "  \"amount\": 999900000000000,\n" +
                        "  \"height\": 22654\n" +
                        "}", false);

        Lease jsonTx = new Lease(json);
        assertEquals(85, jsonTx.toBinary().length);
    }
}
