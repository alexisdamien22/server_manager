package com.alexisdamien22.servermanager.http;

import com.sun.net.httpserver.HttpExchange;

public final class HttpUtils {

    private HttpUtils() {
    }

    public static void addCorsHeaders(
            HttpExchange exchange) {

        exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.getResponseHeaders().add(
                "Access-Control-Allow-Methods",
                "GET,POST,OPTIONS"
        );

        exchange.getResponseHeaders().add(
                "Access-Control-Allow-Headers",
                "*"
        );
    }
}