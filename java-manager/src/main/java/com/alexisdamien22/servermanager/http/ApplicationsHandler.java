package com.alexisdamien22.servermanager.http;

import com.alexisdamien22.servermanager.application.ApplicationManager;
import com.alexisdamien22.servermanager.config.ApplicationConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ApplicationsHandler
    implements HttpHandler {

    private final ApplicationManager applicationManager;

    public ApplicationsHandler(
        ApplicationManager applicationManager) {

        this.applicationManager =
            applicationManager;
    }

    @Override
    public void handle(HttpExchange exchange)
        throws IOException {
        
        HttpUtils.addCorsHeaders(
            exchange
        );

        StringBuilder json =
            new StringBuilder("[");

        boolean first = true;

        for (ApplicationConfig app :
                applicationManager.getConfiguredApplications()) {

            if (!first) {
                json.append(",");
            }

            boolean running =
                    applicationManager.isRunning(
                            app.getId()
                    );

            json.append("{")
                .append("\"id\":\"")
                .append(app.getId())
                .append("\",")

                .append("\"name\":\"")
                .append(app.getName())
                .append("\",")

                .append("\"type\":\"")
                .append(app.getType())
                .append("\",")

                .append("\"running\":")
                .append(running)

                .append("}");

            first = false;
        }

        json.append("]");

        String response = json.toString();

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