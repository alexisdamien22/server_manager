package com.alexisdamien22.servermanager.application;

import com.alexisdamien22.servermanager.config.ApplicationConfig;

import java.io.File;
import java.io.IOException;

public class ApplicationLauncher {

    public Process launch(
            ApplicationConfig config)
            throws IOException {

        ProcessBuilder builder =
                new ProcessBuilder(
                        config.getStartupCommand()
                );

        builder.directory(
                new File(
                        config.getWorkingDirectory()
                )
        );

        builder.redirectErrorStream(true);

        return builder.start();
    }
}