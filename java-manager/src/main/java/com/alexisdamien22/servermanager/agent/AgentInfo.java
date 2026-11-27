package com.alexisdamien22.servermanager.agent;

public class AgentInfo {

    private final long agentId;

    private final String token;

    public AgentInfo(
            long agentId,
            String token) {

        this.agentId = agentId;
        this.token = token;
    }

    public long getAgentId() {
        return agentId;
    }

    public String getToken() {
        return token;
    }
}