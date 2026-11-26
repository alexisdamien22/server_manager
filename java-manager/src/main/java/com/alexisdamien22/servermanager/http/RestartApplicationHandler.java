package com.alexisdamien22.servermanager.http;

import com.alexisdamien22.servermanager.application.ApplicationManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RestartApplicationHandler
        implements HttpHandler {

    private final ApplicationManager applicationManager;

    public RestartApplicationHandler(
            ApplicationManager applicationManager) {

        this.applicationManager = applicationManager;
    }

    @Override
    public void handle(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            exchange.sendResponseHeaders(
                    405,
                    -1
            );

            return;
        }

        String query =
                exchange.getRequestURI()
                        .getQuery();

        if (query == null
                || !query.startsWith("id=")) {

            exchange.sendResponseHeaders(
                    400,
                    -1
            );

            return;
        }

        String id =
                query.substring(3);

        applicationManager
                .restartApplication(id);

        String response =
                "{\"status\":\"restarted\"}";

        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders()
                .add(
                        "Content-Type",
                        "application/json"
                );

        exchange.sendResponseHeaders(
                200,
                bytes.length
        );

        try (OutputStream os =
                     exchange.getResponseBody()) {

            os.write(bytes);
        }
    }
}