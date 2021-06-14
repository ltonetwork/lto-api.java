package com.ltonetwork.client.core;

import com.ltonetwork.client.utils.HttpClientUtil;
import com.ltonetwork.client.utils.JsonObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

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
}