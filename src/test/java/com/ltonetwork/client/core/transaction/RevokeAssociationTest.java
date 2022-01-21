package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.TestUtil;
import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;


public class RevokeAssociationTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    RevokeAssociation tx;

    @Before
    public void init() {
        tx = new RevokeAssociation(new Address("3N3Cn2pYtqzj7N9pviSesNe8KG9Cmb718Y1"), 1, "hash", Encoding.RAW);
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
        assertEquals("3fkSoZ", tx.getHash());
    }

    @Test
    public void testCreateNoHash() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Can't get hash; missing");

        Account account = TestUtil.createAccount();

        RevokeAssociation txNoHash = new RevokeAssociation(new Address("3N3Cn2pYtqzj7N9pviSesNe8KG9Cmb718Y1"), 1);
        txNoHash.signWith(account);

        assertEquals(82, txNoHash.toBinary().length);

        txNoHash.getHash();
    }

    @Test
    public void testCreateWithJson() {
        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 17,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"signature\": \"5Ae37E2XfWXYPSgLp1TTM69noSWnDJrRGgk2Pb6aSptDdzU2yteitoYfzk91x5oRuT3BNhu1zFyJ9iND4RbFUbBk\",\n" +
                        "  \"version\": 1,\n" +
                        "  \"recipient\": \"3Mv7ajrPLKewkBNqfxwRZoRwW6fziehp7dQ\",\n" +
                        "  \"party\": \"3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx\",\n" +
                        "  \"associationType\": \"1\",\n" +
                        "  \"hash\": \"3fkSoZ\",\n" + //base58 of "hash"
                        "  \"height\": 22654\n" +
                        "}", false);

        RevokeAssociation jsonTx = new RevokeAssociation(json);
        assertEquals(88, jsonTx.toBinary().length);
    }

    @Test
    public void testCreateWithJsonNoHash() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Can't get hash; missing");

        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 17,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx\",\n" +
                        "  \"senderPublicKey\": \"FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 1,\n" +
                        "  \"recipient\": \"3Mv7ajrPLKewkBNqfxwRZoRwW6fziehp7dQ\",\n" +
                        "  \"party\": \"3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx\",\n" +
                        "  \"associationType\": \"1\",\n" +
                        "  \"height\": 22654\n" +
                        "}", false);

        RevokeAssociation jsonTx = new RevokeAssociation(json);
        assertEquals(82, jsonTx.toBinary().length);

        jsonTx.getHash();
    }
}
