package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.TestUtil;
import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.core.transacton.SetScript;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.types.JsonObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;


public class SetScriptTest {
    SetScript tx;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void init() {
        tx = new SetScript("3MsE8Jfjkh2zaZ1LCGqaDzB5nAYw5FXhfCx");
    }

    @Test
    public void testToBinary() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        assertEquals(190, tx.toBinary().length);
    }

    @Test
    public void testToBinaryFail() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Sender public key not set");

        tx.toBinary();
    }

    @Test
    public void testGetEstimatedFeeFail() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Can't estimate fee; the script hasn't been compiled");

        tx.getEstimatedFee();
    }

    @Test
    public void testGetComplexityFail() {
        expectedEx.expect(BadMethodCallException.class);
        expectedEx.expectMessage("Can't fetch complexity; the script hasn't been compiled");

        tx.getComplexity();
    }

    @Test
    public void testCreateWithJson() {
        SetScript jsonTx = createFromJson();
        assertEquals(84, jsonTx.toBinary().length);
    }

    @Test
    public void testGetComplexity() {
        SetScript jsonTx = createFromJson();
        assertEquals(100, jsonTx.getComplexity());
    }

    @Test
    public void testGetEstimatedFee() {
        SetScript jsonTx = createFromJson();
        assertEquals(10 + 500_000_000, jsonTx.getEstimatedFee());
    }

    private SetScript createFromJson() {
        return new SetScript(new JsonObject(
                "{\n" +
                        "  \"type\": 13,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 1,\n" +
                        "  \"extraFee\": 10,\n" +
                        "  \"complexity\": 100,\n" +
                        "  \"recipient\": \"3Mv7ajrPLKewkBNqfxwRZoRwW6fziehp7dQ\",\n" +
                        "  \"script\": \"script\",\n" +
                        "  \"height\": 22654\n" +
                        "}", false));
    }
}
