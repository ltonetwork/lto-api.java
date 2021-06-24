package com.ltonetwork.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;

public class HttpClientUtil {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpResponse<String> get(URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();
        return sendRequest(request);
    }

    public static HttpResponse<String> get(URI uri, Map<String, String> headers) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .GET();

        addHeaders(requestBuilder, headers);

        HttpRequest request = requestBuilder.build();

        return sendRequest(request);
    }

    public static HttpResponse<String> delete(URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .DELETE()
                .build();
        return sendRequest(request);
    }

    public static HttpResponse<String> delete(URI uri, Map<String, String> headers) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .DELETE();

        addHeaders(requestBuilder, headers);

        HttpRequest request = requestBuilder.build();

        return sendRequest(request);
    }

    public static HttpResponse<String> post(URI uri, Map<String, Object> params) {
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
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return sendRequest(request);
    }

    public static HttpResponse<String> post(URI uri, Map<String, Object> params, Map<String, String> headers) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";

        try {
            requestBody = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(params);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        addHeaders(requestBuilder, headers);

        HttpRequest request = requestBuilder.build();

        return sendRequest(request);
    }

    public static HttpResponse<String> postScript(URI uri, String script) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";

        try {
            requestBody = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(script);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        HttpRequest request = requestBuilder.build();

        return sendRequest(request);
    }

    public static HttpResponse<String> postScript(URI uri, String script, Map<String, String> headers) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";

        try {
            requestBody = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(script);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        addHeaders(requestBuilder, headers);

        HttpRequest request = requestBuilder.build();

        return sendRequest(request);
    }

    private static HttpResponse<String> sendRequest(HttpRequest request) {
        HttpResponse<String> resp = null;
        try {
            resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return resp;
    }

    private static void addHeaders(HttpRequest.Builder requestBuilder, Map<String, String> headers) {
        ArrayList<String> keys = new ArrayList<String>(headers.keySet());
        ArrayList<String> values = new ArrayList<String>(headers.values());

        for (int i = 0; i < keys.size(); i++) {
            requestBuilder.header(keys.get(i), values.get(i));
        }
    }
}
