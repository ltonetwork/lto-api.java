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

import static org.junit.Assert.*;


public class TransferTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    Transfer tx;

    @Before
    public void init() {
        tx = new Transfer(1, new Address("3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx"));
    }

    @Test
    public void testSignWith() {
        Account account = TestUtil.createAccount();

        assertFalse(tx.isSigned());
        tx.signWith(account);
        assertTrue(tx.isSigned());
    }

    @Test
    public void testToBinaryNoAttachment() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        assertEquals(88, tx.toBinary().length);
    }

    @Test
    public void testToBinaryV1() {
        Account account = TestUtil.createAccount();
        Transfer txV1 = new Transfer(1, new Address("3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx"), (byte) 1);
        txV1.signWith(account);
        txV1.setAttachment("test");

        assertEquals(88, txV1.toBinary().length);
    }

    @Test
    public void testToBinaryV2() {
        Account account = TestUtil.createAccount();
        Transfer txV2 = new Transfer(1, new Address("3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx"), (byte) 2);
        txV2.signWith(account);
        txV2.setAttachment("test");

        assertEquals(89, txV2.toBinary().length);
    }

    @Test
    public void testToBinaryV3() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);
        tx.setAttachment("test");

        assertEquals(91, tx.toBinary().length);
    }

    @Test
    public void testToBinaryFail() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Sender public key not set");

        tx.toBinary();
    }

    @Test
    public void testGetNetwork() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        assertEquals((byte) 84, tx.getNetwork());
    }

    @Test
    public void testCreateWithJson() {
        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 4,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 1,\n" +
                        "  \"recipient\": \"3Mv7ajrPLKewkBNqfxwRZoRwW6fziehp7dQ\",\n" +
                        "  \"amount\": 999900000000000,\n" +
                        "  \"attachment\": \"\",\n" +
                        "  \"height\": 22654\n" +
                        "}", false);

        Transfer jsonTx = new Transfer(json);
        assertEquals(85, jsonTx.toBinary().length);
    }

    @Test
    public void testAddSponsor() {
        Account sender = TestUtil.createAccount();
        Account sponsor = TestUtil.createAccount();

        tx.signWith(sender);
        tx.sponsorWith(sponsor);
        assertEquals(sponsor.getAddress(), tx.getSponsor().getAddress());
        assertEquals(2, tx.getProofs().size());
    }

    @Test
    public void testAddSponsorFail() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Transaction should be signed by the sender before adding a sponsor");

        Account sponsor = TestUtil.createAccount();

        tx.sponsorWith(sponsor);
    }
}
