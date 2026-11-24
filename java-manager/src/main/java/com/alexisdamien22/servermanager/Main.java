package com.alexisdamien22.servermanager;

import com.alexisdamien22.servermanager.application.ApplicationManager;
import com.alexisdamien22.servermanager.config.ApplicationConfig;
import com.alexisdamien22.servermanager.config.ConfigLoader;
import com.alexisdamien22.servermanager.config.ManagerConfig;
import com.alexisdamien22.servermanager.http.HealthHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args)
            throws Exception {

        System.out.println(
                "Starting Manager..."
        );

        ConfigLoader loader =
                new ConfigLoader();

        ManagerConfig config =
                loader.load();

        ApplicationManager applicationManager =
                new ApplicationManager();

        for (ApplicationConfig application :
                config.getApplications()) {

            if (application.isAutoStart()) {

                System.out.println(
                        "Starting application: "
                                + application.getId()
                );

                applicationManager.startApplication(
                        application
                );
            }
        }

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