package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.TestUtil;
import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.core.transacton.Association;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;


public class AssociationTest {
    byte chainId;
    Association tx;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void init() {
        tx = new Association(new Address("3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx"), 1, "hash", Encoding.RAW);
    }

    @Test
    public void testToBinary() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        assertEquals(88, tx.toBinary().length);
    }

    @Test
    public void testToBinaryFail() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Sender public key not set");

        tx.toBinary();
    }

    @Test
    public void testGetHash() {
        assertEquals("EeNE3sD", tx.getHash());
    }

    @Test
    public void testCreateNoHash() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Can't get hash; missing");

        Account account = TestUtil.createAccount();

        Association txNoHash = new Association(new Address("3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx"), 1);
        txNoHash.signWith(account);

        assertEquals(82, txNoHash.toBinary().length);

        txNoHash.getHash();
    }

    @Test
    public void testCreateWithJson() {
        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 16,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 1,\n" +
                        "  \"recipient\": \"3Mv7ajrPLKewkBNqfxwRZoRwW6fziehp7dQ\",\n" +
                        "  \"party\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"associationType\": \"1\",\n" +
                        "  \"hash\": \"3fkSoZ\",\n" + //base58 of "hash"
                        "  \"height\": 22654\n" +
                        "}", false);

        Association jsonTx = new Association(json);
        assertEquals(88, jsonTx.toBinary().length);
    }

    @Test
    public void testCreateWithJsonNoHash() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Can't get hash; missing");

        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 16,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 1,\n" +
                        "  \"recipient\": \"3Mv7ajrPLKewkBNqfxwRZoRwW6fziehp7dQ\",\n" +
                        "  \"party\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"associationType\": \"1\",\n" +
                        "  \"height\": 22654\n" +
                        "}", false);

        Association jsonTx = new Association(json);
        assertEquals(82, jsonTx.toBinary().length);

        jsonTx.getHash();
    }
}
