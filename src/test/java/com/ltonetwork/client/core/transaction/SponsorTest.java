package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.TestUtil;
import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.core.transacton.Sponsor;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.JsonObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;


public class SponsorTest {
    byte chainId;
    Sponsor tx;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void init() {
        chainId = 84;
        tx = new Sponsor(new Address("3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx", chainId));
    }

    @Test
    public void testToBinary() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        assertEquals(186, tx.toBinary().length);
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
                        "  \"type\": 18,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 1,\n" +
                        "  \"recipient\": \"3Mv7ajrPLKewkBNqfxwRZoRwW6fziehp7dQ\",\n" +
                        "  \"height\": 22654\n" +
                        "}", false);

        Sponsor jsonTx = new Sponsor(json);
        assertEquals(102, jsonTx.toBinary().length);
    }
}
