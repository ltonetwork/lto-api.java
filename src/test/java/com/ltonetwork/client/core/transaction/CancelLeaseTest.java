package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.TestUtil;
import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.core.transacton.CancelLease;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.JsonObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;


public class CancelLeaseTest {
    CancelLease tx;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void init() {
        tx = new CancelLease("3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx");
    }

    @Test
    public void testToBinary() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        assertEquals(160, tx.toBinary().length);
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
                        "  \"type\": 9,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 2,\n" +
                        "  \"leaseId\": \"3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx\",\n" +
                        "  \"height\": 22654\n" +
                        "}", false);

        CancelLease jsonTx = new CancelLease(json);
        assertEquals(76, jsonTx.toBinary().length);
    }
}
