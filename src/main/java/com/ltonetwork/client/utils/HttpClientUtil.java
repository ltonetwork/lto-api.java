package com.ltonetwork.client.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class HttpClientUtil {

    private static HttpClient client;

    public static void get(URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .build();
    }

    public static void get(URI uri, HashMap<String, String> headers) {
        ArrayList<String> keys = new ArrayList<String>(headers.keySet());
        ArrayList<String> values = new ArrayList<String>(headers.values());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json");

        for(int i=0; i<keys.size(); i++){
            requestBuilder.header(keys.get(i), values.get(i));
        }

        HttpRequest request = requestBuilder.build();
    }

}
