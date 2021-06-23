package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.TestUtil;
import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.core.transacton.Transfer;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.*;
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
        Account account = TestUtil.createAccount();

        assertFalse(tx.isSigned());
        tx.signWith(account);
        assertTrue(tx.isSigned());
    }

    @Test
    public void testToBinaryNoAttachment() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        assertEquals(110, tx.toBinary().length);
    }

    @Test
    public void testToBinary() {
        Account account = TestUtil.createAccount();
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
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        byte exp = 84;
        assertEquals(exp, tx.getNetwork());
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
                "  \"signature\": \"5Ae37E2XfWXYPSgLp1TTM69noSWnDJrRGgk2Pb6aSptDdzU2yteitoYfzk91x5oRuT3BNhu1zFyJ9iND4RbFUbBk\",\n" +
                "  \"version\": 1,\n" +
                "  \"recipient\": \"3Mv7ajrPLKewkBNqfxwRZoRwW6fziehp7dQ\",\n" +
                "  \"amount\": 999900000000000,\n" +
                "  \"attachment\": \"\",\n" +
                "  \"height\": 22654\n" +
                "}", false);

        Transfer jsonTx = new Transfer(json);
        assertEquals(110, jsonTx.toBinary().length);
    }
}
