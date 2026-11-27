package com.alexisdamien22.servermanager;

import com.alexisdamien22.servermanager.agent.AgentInfo;
import com.alexisdamien22.servermanager.agent.AgentRegistrationService;
import com.alexisdamien22.servermanager.application.ApplicationManager;
import com.alexisdamien22.servermanager.config.ApplicationConfig;
import com.alexisdamien22.servermanager.config.ConfigLoader;
import com.alexisdamien22.servermanager.config.ManagerConfig;
import com.alexisdamien22.servermanager.http.ApplicationStatusHandler;
import com.alexisdamien22.servermanager.http.ApplicationsHandler;
import com.alexisdamien22.servermanager.http.HealthHandler;
import com.alexisdamien22.servermanager.http.StartApplicationHandler;
import com.alexisdamien22.servermanager.http.StopApplicationHandler;
import com.alexisdamien22.servermanager.http.ApplicationLogsHandler;
import com.alexisdamien22.servermanager.http.RestartApplicationHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args)
            throws Exception {

        ConfigLoader loader =
            new ConfigLoader();

        ManagerConfig config =
            loader.load();

        ApplicationManager applicationManager =
            new ApplicationManager(config);
        
        try {

            AgentRegistrationService
                registrationService =
                new AgentRegistrationService(
                    config
                        .getApi()
                        .getUrl(),

                    config
                        .getAgent()
                        .getName()
                );

            AgentInfo agentInfo =
                    registrationService
                            .register();

            System.out.println(
                "Registered agent "
                + agentInfo.getAgentId()
            );

        } catch (Exception exception) {

            System.out.println(
                "Agent registration failed"
            );

            exception.printStackTrace();
        }

        for (ApplicationConfig application :
                config.getApplications()) {

            if (application.isAutoStart()) {
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

        server.createContext(
            "/applications",
            new ApplicationsHandler(
                applicationManager
            )
        );

        server.createContext(
            "/applications/start",
            new StartApplicationHandler(
                applicationManager
            )
        );

        server.createContext(
            "/applications/status",
            new ApplicationStatusHandler(
                applicationManager
            )
        );

        server.createContext(
            "/applications/stop",
            new StopApplicationHandler(
                applicationManager
            )
        );

        server.createContext(
            "/applications/restart",
            new RestartApplicationHandler(
                applicationManager
            )
        );

        server.createContext(
            "/applications/logs",
            new ApplicationLogsHandler(
                applicationManager
            )
        );

        server.start();
    }
}