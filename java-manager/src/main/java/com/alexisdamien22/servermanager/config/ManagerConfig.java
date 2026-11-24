package com.alexisdamien22.servermanager.config;

import java.util.List;

public class ManagerConfig {

    private List<ServerConfig> servers;

    public List<ServerConfig> getServers() {
        return servers;
    }

    public void setServers(List<ServerConfig> servers) {
        this.servers = servers;
    }
}