package com.alexisdamien22.servermanager.config;

import java.util.List;

public class ApplicationConfig {

   private String id;

    private String name;

    private String type;

    private String workingDirectory;

    private List<String> startupCommand;

    private boolean autoStart;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public List<String> getStartupCommand() {
        return startupCommand;
    }

    public void setStartupCommand(List<String> startupCommand) {
        this.startupCommand = startupCommand;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }
}