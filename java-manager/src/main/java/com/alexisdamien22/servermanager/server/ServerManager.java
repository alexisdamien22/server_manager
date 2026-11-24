package com.alexisdamien22.servermanager.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {

    private final Map<String, ManagedServer>
            runningServers =
                new ConcurrentHashMap<>();

}