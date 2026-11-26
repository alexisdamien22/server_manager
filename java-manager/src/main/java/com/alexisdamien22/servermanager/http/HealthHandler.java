package com.alexisdamien22.servermanager.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HealthHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange)
        throws IOException {
        
        HttpUtils.addCorsHeaders(
            exchange
        );

        String response = "{\"status\":\"UP\"}";

        exchange.getResponseHeaders()
            .add("Content-Type", "application/json");

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