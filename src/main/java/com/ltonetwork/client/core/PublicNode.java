package com.ltonetwork.client.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltonetwork.client.core.transaction.*;
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

    public Transaction getTransaction(String id) {
        HttpResponse<String> resp = HttpClientUtil.get(URI.create(String.format("%s/transactions/info/%s", this.uri.toString(), id)));
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
        Transaction ret;

        switch ((int) json.get("type")) {
            case 4:
                ret = new Transfer(json);
                break;
            case 8:
                ret = new Lease(json);
                break;
            case 9:
                ret = new CancelLease(json);
                break;
            case 11:
                ret = new MassTransfer(json);
                break;
            case 13:
                ret = new SetScript(json);
                break;
            case 15:
                ret = new Anchor(json);
                break;
            case 16:
                ret = new Association(json);
                break;
            case 17:
                ret = new RevokeAssociation(json);
                break;
            case 18:
                ret = new Sponsor(json);
                break;
            case 19:
                ret = new CancelSponsor(json);
                break;
            default:
                throw new InvalidArgumentException("Unknown transaction type");
        }

        return ret;
    }
}
