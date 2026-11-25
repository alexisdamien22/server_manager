package com.alexisdamien22.servermanager.http;

import com.alexisdamien22.servermanager.application.ApplicationManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StopApplicationHandler
    implements HttpHandler {

    private final ApplicationManager applicationManager;

    public StopApplicationHandler(
        ApplicationManager applicationManager) {

        this.applicationManager =
            applicationManager;
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

        if (query == null ||
                !query.startsWith("id=")) {

            exchange.sendResponseHeaders(
                400,
                -1
            );

            return;
        }

        String id =
            query.substring(3);

        applicationManager.stopApplication(id);

        String response =
            "{\"status\":\"stopped\"}";

        exchange.sendResponseHeaders(
            200,
            response.getBytes().length
        );

        try (OutputStream os =
            exchange.getResponseBody()) {

            os.write(
                response.getBytes(
                    StandardCharsets.UTF_8
                )
            );
        }
    }
}