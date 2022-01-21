package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.TestUtil;
import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.core.transacton.Register;
import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.types.Key;
import com.ltonetwork.client.types.PublicKey;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;


public class RegisterTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    Register tx;
    // type + version + chainId + timestamp + publicKey + fee + accountsEntriesLength
    int DEFAULT_BINARY_LENGTH = 1 + 1 + 1 + 8 + 32 + 8 + 2;

    @Before
    public void init() {
        tx = new Register();
        tx.addAccount(new PublicKey("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y", Encoding.BASE58));
        tx.addAccount(new PublicKey("0259d798aca3e80eb68ab974e0979e1863977ce3fd72238b9f99780b367fdd72f1", Encoding.HEX, Key.KeyType.SECP256K1));
    }

    @Test
    public void testToBinary() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        // ed25519 key (keyType + key) - 33
        // secp256k1 key (keyType + key) - 34
        assertEquals(DEFAULT_BINARY_LENGTH + (1 + 32) + (1 + 33), tx.toBinary().length);
    }

    @Test
    public void testToBinaryNoData() {
        Account account = TestUtil.createAccount();
        tx = new Register();
        tx.signWith(account);

        assertEquals(DEFAULT_BINARY_LENGTH, tx.toBinary().length);
    }

    @Test
    public void testCreateWithJson() {
        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 20,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 3,\n" +
                        "  \"accounts\": [" +
                        "    {\"keyType\": \"ed25519\", \"publicKey\": \"FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y\"}," +
                        "    {\"keyType\": \"secp256k1\", \"publicKey\": \"hWLhenzapwF5i76sFneNMikyaLt7fQQ7cpbgtKbruJzt\"}" +
                        "  ],\n" +
                        "}", false);

        Register jsonTx = new Register(json);

        // ed25519 key (keyType + key) - 33
        // secp256k1 key (keyType + key) - 34
        assertEquals(DEFAULT_BINARY_LENGTH + (1 + 32) + (1 + 33), jsonTx.toBinary().length);
    }

    @Test
    public void testCreateWithJsonNoData() {
        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 20,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 3,\n" +
                        "  \"accounts\": [" +
                        "  ],\n" +
                        "}", false);

        Register jsonTx = new Register(json);

        assertEquals(DEFAULT_BINARY_LENGTH, jsonTx.toBinary().length);
    }
}
