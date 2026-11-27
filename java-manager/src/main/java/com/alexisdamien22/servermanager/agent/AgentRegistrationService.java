package com.alexisdamien22.servermanager.agent;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;

public class AgentRegistrationService {

    private final String apiUrl;

    private final String agentName;

    public AgentRegistrationService(
            String apiUrl,
            String agentName) {

        this.apiUrl = apiUrl;
        this.agentName = agentName;
    }

    public AgentInfo register()
            throws IOException {

        URL url =
                new URL(
                        apiUrl
                        + "/api/agents/register"
                );

        HttpURLConnection connection =
                (HttpURLConnection)
                        url.openConnection();

        connection.setRequestMethod(
                "POST"
        );

        connection.setDoOutput(
                true
        );

        connection.setRequestProperty(
                "Content-Type",
                "application/json"
        );

        String payload =
                """
                {
                    "name":"%s",
                    "version":"0.3.0"
                }
                """
                .formatted(
                        agentName
                );

        connection
                .getOutputStream()
                .write(
                        payload.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        if (connection.getResponseCode()
                != 200) {

            throw new IOException(
                    "Registration failed: "
                    + connection.getResponseCode()
            );
        }

        String response =
                new String(
                        connection
                                .getInputStream()
                                .readAllBytes(),
                        StandardCharsets.UTF_8
                );

        long agentId =
                extractLong(
                        response,
                        "agentId"
                );

        String token =
                extractString(
                        response,
                        "token"
                );

        return new AgentInfo(
                agentId,
                token
        );
    }

    private long extractLong(
            String json,
            String key) {

        String value =
                json.replaceAll(
                        ".*\"" + key
                                + "\"\\s*:\\s*([0-9]+).*",
                        "$1"
                );

        return Long.parseLong(
                value
        );
    }

    private String extractString(
        String json,
        String key) {

        String value =
            json.replaceAll(
                "(?s).*\"" + key
                    + "\"\\s*:\\s*\"([^\"]+)\".*",
                "$1"
            ).trim();

        if (value.equals(json.trim())) {
            throw new IllegalArgumentException(
                "Missing JSON field: " + key
            );
        }

        return value;
    }
}