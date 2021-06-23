package com.ltonetwork.client.core;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class PublicNodeTest {

    URI uri = URI.create("https://testnet.lto.network");
    String apiKey = "secret";

    PublicNode pb = new PublicNode(uri, apiKey);

    @Test
    public void testGetUri() {
        assertEquals(URI.create("https://testnet.lto.network"), pb.getUri());
    }

    @Test
    public void testGetApiKey() {
        assertEquals("secret", pb.getApiKey());
    }
}
