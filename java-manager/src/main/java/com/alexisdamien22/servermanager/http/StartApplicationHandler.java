package com.alexisdamien22.servermanager.http;

import com.alexisdamien22.servermanager.application.ApplicationManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StartApplicationHandler
        implements HttpHandler {

    private final ApplicationManager manager;

    public StartApplicationHandler(
            ApplicationManager manager) {

        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange)
            throws IOException {

        String query =
                exchange.getRequestURI()
                        .getQuery();

        String id = query.replace(
                "id=",
                ""
        );

        manager.startApplication(id);

        String response =
                "{\"status\":\"started\"}";

        exchange.sendResponseHeaders(
                200,
                response.length()
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