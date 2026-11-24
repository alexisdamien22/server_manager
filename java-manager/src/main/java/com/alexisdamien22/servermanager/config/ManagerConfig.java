package com.alexisdamien22.servermanager.config;

import java.util.List;

public class ManagerConfig {

    private List<ApplicationConfig> applications;

    public List<ApplicationConfig> getApplications() {
        return applications;
    }

    public void setApplications(
            List<ApplicationConfig> applications) {
        this.applications = applications;
    }
}