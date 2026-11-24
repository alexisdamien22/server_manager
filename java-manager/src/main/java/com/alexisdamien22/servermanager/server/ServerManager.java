package com.alexisdamien22.servermanager.server;

public class ServerManager {

    private final Map<String, ManagedServer>
            runningServers =
                new ConcurrentHashMap<>();

}