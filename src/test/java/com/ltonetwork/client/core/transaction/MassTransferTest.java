package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.TestUtil;
import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.core.transacton.MassTransfer;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.Address;
import com.ltonetwork.client.types.JsonObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class MassTransferTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    MassTransfer tx;

    @Before
    public void init() {
        tx = new MassTransfer();
    }

    @Test
    public void testToBinaryNoTransferNoAttachment() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        assertEquals(52, tx.toBinary().length);
    }

    @Test
    public void testToBinary() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);
        tx.setAttachment("test");

        assertEquals(58, tx.toBinary().length);
    }

    @Test
    public void testAddTransfer() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        int old = tx.toBinary().length;
        tx.addTransfer(new Address("3MwGRJ1cbCQgP3mSGMR6pR1EJzXAD3e6Bvu"), 1);
        assertTrue(tx.toBinary().length > old);
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
                        "  \"type\": 11,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 1,\n" +
                        "  \"transfers\": [{\n" +
                        "      \"recipient\": \"3MwGRJ1cbCQgP3mSGMR6pR1EJzXAD3e6Bvu\",\n" +
                        "      \"amount\": 100000000\n" +
                        "    }],\n" +
                        "  \"attachment\": \"3yZe7d\",\n" + // base58 encoded "test"
                        "  \"height\": 22654\n" +
                        "}", false);

        MassTransfer jsonTx = new MassTransfer(json);
        assertEquals(92, jsonTx.toBinary().length);
    }

    @Test
    public void testCreateWithJsonNoTransferNoAttachment() {
        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 11,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 1,\n" +
                        "  \"transfers\": [],\n" +
                        "  \"height\": 22654\n" +
                        "}", false);

        MassTransfer jsonTx = new MassTransfer(json);
        assertEquals(52, jsonTx.toBinary().length);
    }

    @Test
    public void testCreateWithJsonNoTransfer() {
        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 11,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 1,\n" +
                        "  \"attachment\": \"3yZe7d\",\n" + // base58 encoded "test"
                        "  \"transfers\": [],\n" +
                        "  \"height\": 22654\n" +
                        "}", false);

        MassTransfer jsonTx = new MassTransfer(json);
        assertEquals(58, jsonTx.toBinary().length);
    }

    @Test
    public void testCreateWithJsonNoAttachment() {
        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 11,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 1,\n" +
                        "  \"transfers\": [{\n" +
                        "      \"recipient\": \"3MwGRJ1cbCQgP3mSGMR6pR1EJzXAD3e6Bvu\",\n" +
                        "      \"amount\": 100000000\n" +
                        "    }],\n" +
                        "  \"height\": 22654\n" +
                        "}", false);

        MassTransfer jsonTx = new MassTransfer(json);
        assertEquals(86, jsonTx.toBinary().length);
    }
}
