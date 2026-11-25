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

        StringBuilder logsBuilder = new StringBuilder();
        logsBuilder.append("[");
        for (int i = 0; i < logs.size(); i++) {
            ConsoleMessage log = logs.get(i);
            logsBuilder.append("\"").append(log.getMessage()).append("\"");
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
                    id,
                    logsBuilder.toString()
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