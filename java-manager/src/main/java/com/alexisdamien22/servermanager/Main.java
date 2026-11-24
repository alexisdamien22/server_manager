package com.alexisdamien22.servermanager;

import com.alexisdamien22.servermanager.http.HealthHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args)
            throws Exception {

        System.out.println(
            "Starting Manager..."
        );

        HttpServer server =
            HttpServer.create(
                new InetSocketAddress(9000),
                0
            );

        server.createContext(
            "/health",
            new HealthHandler()
        );

        server.start();

        System.out.println(
            "Manager started on port 9000"
        );
    }
}