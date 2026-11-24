package com.alexisdamien22.servermanager.server;

import com.alexisdamien22.servermanager.config.ServerConfig;

public class ManagedServer {

    private ServerConfig config;
    private Process process;

    public ManagedServer(
            ServerConfig config,
            Process process) {

        this.config = config;
        this.process = process;
    }

    public String getId() {
        return config.getId();
    }

    public ServerConfig getConfig() {
        return config;
    }

    public Process getProcess() {
        return process;
    }
}