package com.alexisdamien22.servermanager.http;

import com.alexisdamien22.servermanager.application.ApplicationManager;
import com.alexisdamien22.servermanager.console.ConsoleMessage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.List;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ApplicationLogsHandler
    implements HttpHandler {

    private final ApplicationManager applicationManager;

    public ApplicationLogsHandler(
        ApplicationManager applicationManager) {

        this.applicationManager = applicationManager;
    }

    @Override
    public void handle(HttpExchange exchange)
        throws IOException {
        
        HttpUtils.addCorsHeaders(
            exchange
        );

        if (!exchange.getRequestMethod()
        .equalsIgnoreCase("GET")) {

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

        List<ConsoleMessage> logs =
            this.applicationManager.getConsoleManager().getMessages(id);

        StringBuilder logsBuilder =
            new StringBuilder();

        logsBuilder.append("[");

        for (int i = 0; i < logs.size(); i++) {

            ConsoleMessage log =
                logs.get(i);

            logsBuilder.append("{")

                .append("\"message\":\"")
                .append(
                    escapeJson(
                        log.getMessage()
                    )
                )
                .append("\",")

                .append("\"timestamp\":\"")
                .append(
                    log.getTimestamp()
                        .toString()
                )
                .append("\"")

                .append("}");

            if (i < logs.size() - 1) {
                logsBuilder.append(",");
            }
        }

        logsBuilder.append("]");

        String response =
            String.format(
                """
                {
                "id":"%s",
                "logs":%s
                }
                """,
                escapeJson(id),
                logsBuilder.toString()
            );

        byte[] bytes =
            response.getBytes(StandardCharsets.UTF_8);

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

    private static String escapeJson(String text) {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}