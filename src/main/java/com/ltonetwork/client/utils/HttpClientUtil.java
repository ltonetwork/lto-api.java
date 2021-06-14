package com.ltonetwork.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;

public class HttpClientUtil {

    private static HttpClient client;

    public static void get(URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    public static void get(URI uri, Map<String, String> headers) {
        ArrayList<String> keys = new ArrayList<String>(headers.keySet());
        ArrayList<String> values = new ArrayList<String>(headers.values());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .GET();

        for(int i=0; i<keys.size(); i++){
            requestBuilder.header(keys.get(i), values.get(i));
        }

        HttpRequest request = requestBuilder.build();
    }

    public static void post(URI uri, Map<String,String> params) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";

        try {
            requestBody = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(params);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    public static void post(URI uri, Map<String,String> params, Map<String,String> headers) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";

        try {
            requestBody = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(params);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ArrayList<String> keys = new ArrayList<String>(headers.keySet());
        ArrayList<String> values = new ArrayList<String>(headers.values());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        for (int i = 0; i < keys.size(); i++) {
            requestBuilder.header(keys.get(i), values.get(i));
        }

        HttpRequest request = requestBuilder.build();
    }
}
