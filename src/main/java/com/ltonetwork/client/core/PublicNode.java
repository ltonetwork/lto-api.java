package com.ltonetwork.client.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltonetwork.client.core.transacton.*;
import com.ltonetwork.client.exceptions.BadMethodCallException;
import com.ltonetwork.client.utils.HttpClientUtil;
import com.ltonetwork.client.utils.JsonObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.Map;

public class PublicNode {
    private final String url;
    private final String apiKey;

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

    public Transaction getTransaction(int id) throws URISyntaxException {
        HttpResponse<String> resp = HttpClientUtil.get(new URI(String.format("%s/transactions/info/%d", this.url, id)));
        return getTransactionObject(new JsonObject(resp.body()));
    }

    public Transaction getUnconfirmed() throws URISyntaxException {
        HttpResponse<String> resp = HttpClientUtil.get(new URI(String.format("%s/transactions/unconfirmed", this.url)));
        return getTransactionObject(new JsonObject(resp.body()));
    }

    public JsonObject compile(String script) throws URISyntaxException {
        HttpResponse<String> resp = HttpClientUtil.postScript(new URI(String.format("%s/utils/script/compile", this.url)), script);
        return new JsonObject(resp.body());
    }

    public JsonObject broadcast(Transaction transaction) throws URISyntaxException {
        if (!transaction.isSigned()) throw new BadMethodCallException("Transaction is not signed");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> tx = objectMapper.convertValue(transaction, Map.class);

        HttpResponse<String> resp = HttpClientUtil.post(new URI(String.format("%s/transactions/broadcast", this.url)), tx);

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
            default -> throw new BadMethodCallException("Unknown transaction type");
        };
    }
}
