package com.alexisdamien22.servermanager.server;

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