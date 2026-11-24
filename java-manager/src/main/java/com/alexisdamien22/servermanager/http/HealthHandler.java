package com.alexisdamien22.servermanager.http;

public class HealthHandler
        implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange)
            throws IOException {

        String response =
                "{\"status\":\"UP\"}";

        exchange.sendResponseHeaders(
                200,
                response.length());

        OutputStream os =
                exchange.getResponseBody();

        os.write(response.getBytes());

        os.close();
    }
}
