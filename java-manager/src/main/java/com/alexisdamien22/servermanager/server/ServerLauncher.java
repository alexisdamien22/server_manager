package com.alexisdamien22.servermanager.server;

import com.alexisdamien22.servermanager.config.ServerConfig;
import java.io.File;
import java.io.IOException;

public class ServerLauncher {

    public Process launch(ServerConfig config)
            throws IOException {

        String[] command =
            config.getStartupCommand().split(" ");

        ProcessBuilder builder =
            new ProcessBuilder(command);

        builder.directory(
            new File(
                config.getWorkingDirectory()
            )
        );

        return builder.start();
    }
}