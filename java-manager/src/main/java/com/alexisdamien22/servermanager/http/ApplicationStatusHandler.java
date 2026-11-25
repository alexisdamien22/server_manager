package com.alexisdamien22.servermanager.http;

import com.alexisdamien22.servermanager.application.ApplicationManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ApplicationStatusHandler
    implements HttpHandler {

    private final ApplicationManager applicationManager;

    public ApplicationStatusHandler(
        ApplicationManager applicationManager) {

        this.applicationManager = applicationManager;
    }

    @Override
    public void handle(HttpExchange exchange)
        throws IOException {

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

        boolean running =
            applicationManager.isRunning(id);

        String response =
            String.format(
                    """
                    {
                      "id":"%s",
                      "running":%s
                    }
                    """,
                    id,
                    running
            );

        exchange.getResponseHeaders()
            .add(
                "Content-Type",
                "application/json"
            );

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