package com.ltonetwork.client.core.transaction;

import com.ltonetwork.client.TestUtil;
import com.ltonetwork.client.core.Account;
import com.ltonetwork.client.types.JsonObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;


public class DataTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    Data tx;
    // type + version + chainId + timestamp + publicKey + fee + dataLength
    int DEFAULT_BINARY_LENGTH = 1 + 1 + 1 + 8 + 33 + 8 + 2;

    @Before
    public void init() {
        tx = new Data(
                new DataEntry[]{
                        new DataEntry<>(DataEntry.DataEntryType.INTEGER, "int", (long) 42),
                        new DataEntry<>(DataEntry.DataEntryType.BOOLEAN, "bool", true),
                        new DataEntry<>(DataEntry.DataEntryType.BINARY, "binary", new byte[]{1, 2, 3}),
                        new DataEntry<>(DataEntry.DataEntryType.STRING, "string", "hello")
                }
        );
    }

    @Test
    public void testToBinaryV3() {
        Account account = TestUtil.createAccount();
        tx.signWith(account);

        // integer (keyLength + key + type + value) - 14
        // bool (keyLength + key + type + value) - 8
        // binary (keyLength + key + type + valueLength + value) - 14
        // string (keyLength + key + type + valueLength + value) - 16
        assertEquals(DEFAULT_BINARY_LENGTH + 14 + 8 + 14 + 16, tx.toBinary().length);
    }

    @Test
    public void testToBinaryNoData() {
        Account account = TestUtil.createAccount();
        tx = new Data(new DataEntry[0]);
        tx.signWith(account);

        assertEquals(DEFAULT_BINARY_LENGTH, tx.toBinary().length);
    }

    @Test
    public void testCreateWithJson() {
        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 12,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderKeyType\": \"ed25519\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 3,\n" +
                        "  \"data\": [" +
                        "    {\"key\": \"int\", \"type\": \"integer\", \"value\": 42}," +
                        "    {\"key\": \"bool\", \"type\": \"boolean\", \"value\": \"true\"}," +
                        "    {\"key\": \"binary\", \"type\": \"binary\", \"value\": [1, 2, 3]}," +
                        "    {\"key\": \"string\", \"type\": \"string\", \"value\": \"hello\"}" +
                        "  ],\n" +
                        "}", false);

        Data jsonTx = new Data(json);

        // integer (keyLength + key + type + value) - 14
        // bool (keyLength + key + type + value) - 8
        // binary (keyLength + key + type + valueLength + value) - 14
        // string (keyLength + key + type + valueLength + value) - 16
        assertEquals(DEFAULT_BINARY_LENGTH + 14 + 8 + 14 + 16, jsonTx.toBinary().length);
    }

    @Test
    public void testCreateWithJsonNoData() {
        JsonObject json = new JsonObject(
                "{\n" +
                        "  \"type\": 12,\n" +
                        "  \"id\": \"oYv8LBTsLRyAq1w7n9UXudAf5Luu9CuRXkYSnxLX2oa\",\n" +
                        "  \"senderKeyType\": \"ed25519\",\n" +
                        "  \"sender\": \"3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy\",\n" +
                        "  \"senderPublicKey\": \"8wFR3b8WnbFaxQEdRnogTqC5doYUrotm3P7upvxPaWUo\",\n" +
                        "  \"fee\": 100000,\n" +
                        "  \"timestamp\": 1538728794530,\n" +
                        "  \"proofs\": [\"65E82MLn6RdF7Y2VrdtFWkHd97teqLSwVdbGyEfy7x6aczkHRDZMvNUfdTAYgqDXzDDKKEkQqVhMVMg6EEEvE3C3\"],\n" +
                        "  \"version\": 3,\n" +
                        "  \"data\": [" +
                        "  ],\n" +
                        "}", false);

        Data jsonTx = new Data(json);

        assertEquals(DEFAULT_BINARY_LENGTH, jsonTx.toBinary().length);
    }
}
