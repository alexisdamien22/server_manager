package com.alexisdamien22.servermanager.application;

import com.alexisdamien22.servermanager.config.ApplicationConfig;
import com.alexisdamien22.servermanager.console.ConsoleReader;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationManager {

    private final Map<String, ManagedApplication>
            runningApplications =
            new ConcurrentHashMap<>();

    private final ApplicationLauncher launcher =
            new ApplicationLauncher();

    public ManagedApplication startApplication(
            ApplicationConfig config)
            throws IOException {

        if (isRunning(config.getId())) {
            return runningApplications.get(
                    config.getId()
            );
        }

        Process process =
                launcher.launch(config);

        Thread consoleThread =
        new Thread(
                new ConsoleReader(
                        config.getId(),
                        process.getInputStream()
                )
        );

        consoleThread.setDaemon(true);

        consoleThread.start();

        ManagedApplication application =
                new ManagedApplication(
                        config,
                        process
                );

        runningApplications.put(
                config.getId(),
                application
        );

        return application;
    }

    public boolean stopApplication(
            String id) {

        ManagedApplication application =
                runningApplications.get(id);

        if (application == null) {
            return false;
        }

        Process process =
                application.getProcess();

        process.destroy();

        runningApplications.remove(id);

        return true;
    }

    public boolean isRunning(
            String id) {

        ManagedApplication application =
                runningApplications.get(id);

        if (application == null) {
            return false;
        }

        if (!application.isRunning()) {

            runningApplications.remove(id);

            return false;
        }

        return true;
    }

    public ManagedApplication getApplication(
            String id) {

        return runningApplications.get(id);
    }

    public int getRunningCount() {

        return runningApplications.size();
    }
}