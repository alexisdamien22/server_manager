package com.alexisdamien22.servermanager.application;

import com.alexisdamien22.servermanager.config.ApplicationConfig;

public class ManagedApplication {

    private final ApplicationConfig config;

    private final Process process;

    public ManagedApplication(
            ApplicationConfig config,
            Process process) {

        this.config = config;
        this.process = process;
    }

    public String getId() {
        return config.getId();
    }

    public String getName() {
        return config.getName();
    }

    public String getType() {
        return config.getType();
    }

    public ApplicationConfig getConfig() {
        return config;
    }

    public Process getProcess() {
        return process;
    }

    public boolean isRunning() {
        return process != null
                && process.isAlive();
    }
}