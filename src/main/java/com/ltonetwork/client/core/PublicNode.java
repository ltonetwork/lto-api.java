package com.ltonetwork.client.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltonetwork.client.core.transacton.*;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.types.JsonObject;
import com.ltonetwork.client.utils.HttpClientUtil;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;

public class PublicNode {
    private final URI uri;
    private final String apiKey;

    public PublicNode(URI uri, String apiKey) {
        this.uri = uri;
        this.apiKey = apiKey;
    }

    public URI getUri() {
        return uri;
    }

    public String getApiKey() {
        return apiKey;
    }

    public Transaction getTransaction(int id) {
        HttpResponse<String> resp = HttpClientUtil.get(URI.create(String.format("%s/transactions/info/%d", this.uri.toString(), id)));
        return getTransactionObject(new JsonObject(resp.body()));
    }

    public Transaction getUnconfirmed() {
        HttpResponse<String> resp = HttpClientUtil.get(URI.create(String.format("%s/transactions/unconfirmed", this.uri.toString())));
        return getTransactionObject(new JsonObject(resp.body()));
    }

    public Transaction compile(String script) {
        HttpResponse<String> resp = HttpClientUtil.postScript(URI.create(String.format("%s/utils/script/compile", this.uri)), script);

        return getTransactionObject(new JsonObject(resp.body()));
    }

    public Transaction broadcast(Transaction transaction) {
        if (!transaction.isSigned()) throw new BadMethodCallException("Transaction is not signed");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> tx = objectMapper.convertValue(transaction, Map.class);

        HttpResponse<String> resp = HttpClientUtil.post(URI.create(String.format("%s/transactions/broadcast", this.uri.toString())), tx);

        return getTransactionObject(new JsonObject(resp.body()));
    }

    public JsonObject get(String endpoint) {
        HttpResponse<String> resp = HttpClientUtil.get(URI.create(uri.toString() + endpoint));
        return new JsonObject(resp.body());
    }

    public JsonObject get(String endpoint, Map<String, String> headers) {
        HttpResponse<String> resp = HttpClientUtil.get(URI.create(uri.toString() + endpoint), headers);
        return new JsonObject(resp.body());
    }

    public JsonObject post(String endpoint, Map<String, Object> params) {
        HttpResponse<String> resp = HttpClientUtil.post(URI.create(uri.toString() + endpoint), params);
        return new JsonObject(resp.body());
    }

    public JsonObject post(String endpoint, Map<String, Object> params, Map<String, String> headers) {
        HttpResponse<String> resp = HttpClientUtil.post(URI.create(uri.toString() + endpoint), params, headers);
        return new JsonObject(resp.body());
    }

    public JsonObject delete(String endpoint) {
        HttpResponse<String> resp = HttpClientUtil.delete(URI.create(uri.toString() + endpoint));
        return new JsonObject(resp.body());
    }

    public JsonObject delete(String endpoint, Map<String, String> headers) {
        HttpResponse<String> resp = HttpClientUtil.delete(URI.create(uri.toString() + endpoint), headers);
        return new JsonObject(resp.body());
    }

    private Transaction getTransactionObject(JsonObject json) {
        return switch ((int) json.get("type")) {
            case 4 -> new Transfer(json);
            case 8 -> new Lease(json);
            case 9 -> new CancelLease(json);
            case 11 -> new MassTransfer(json);
            case 13 -> new SetScript(json);
            case 15 -> new Anchor(json);
            case 16 -> new Association(json);
            case 17 -> new RevokeAssociation(json);
            case 18 -> new Sponsor(json);
            case 19 -> new CancelSponsor(json);
            default -> throw new InvalidArgumentException("Unknown transaction type");
        };
    }
}
