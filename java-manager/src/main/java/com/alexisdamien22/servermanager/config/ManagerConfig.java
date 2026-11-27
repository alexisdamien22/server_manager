package com.alexisdamien22.servermanager.config;

import java.util.List;

public class ManagerConfig {

    private AgentConfig agent;

    private ApiConfig api;

    private List<ApplicationConfig> applications;

    public List<ApplicationConfig> getApplications() {
        return applications;
    }

    public void setApplications(
            List<ApplicationConfig> applications) {
        this.applications = applications;
    }

    public AgentConfig getAgent() {
        return this.agent;
    }

    public void setAgent(AgentConfig agent) {
        this.agent = agent;
    }

    public ApiConfig getApi() {
        return this.api;
    }

    public void setApi(ApiConfig api) {
        this.api = api;
    }
}