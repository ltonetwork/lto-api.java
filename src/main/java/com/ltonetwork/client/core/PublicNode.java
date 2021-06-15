package com.ltonetwork.client.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ltonetwork.client.core.transacton.Transaction;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.utils.HttpClientUtil;
import com.ltonetwork.client.utils.JsonObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.Map;

public class PublicNode {
    private String url;
    private String apiKey;

    public PublicNode(String url, String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
    }

    public String getUrl() {
        return url;
    }

    public String getApiKey() {
        return apiKey;
    }

    //    TODO: Make it return proper transaction and not a JSON.
    public JsonObject getTransaction(int id) throws URISyntaxException {
        HttpResponse<String> resp = HttpClientUtil.get(new URI(String.format("%s/transactions/info/%d", this.url, id)));
        return new JsonObject(resp.body());
    }

    //    TODO: Make it return proper transaction and not a JSON.
    public JsonObject getUnconfirmed() throws URISyntaxException {
        HttpResponse<String> resp = HttpClientUtil.get(new URI(String.format("%s/transactions/unconfirmed", this.url)));
        return new JsonObject(resp.body());
    }

    public JsonObject compile(String script) throws URISyntaxException {
        HttpResponse<String> resp = HttpClientUtil.postScript(new URI(String.format("%s/utils/script/compile", this.url)), script);
        return new JsonObject(resp.body());
    }

    public JsonObject broadcast(Transaction transaction) throws URISyntaxException {
        if(!transaction.isSigned()) throw new BadMethodCallException("Transaction is not signed");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> tx = objectMapper.convertValue(transaction, Map.class);

        HttpResponse<String> resp = HttpClientUtil.post(new URI(String.format("%s/transactions/broadcast", this.url)), tx);

        return new JsonObject(resp.body());
    }
}
