package com.alexisdamien22.servermanager.console;

import com.alexisdamien22.servermanager.console.ConsoleManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConsoleReader
        implements Runnable {

    private final String applicationId;

    private final ConsoleManager consoleManager;

    private final InputStream inputStream;

    public ConsoleReader(
        String applicationId,
        InputStream inputStream,
        ConsoleManager consoleManager) {

        this.applicationId = applicationId;
        this.inputStream = inputStream;
        this.consoleManager = consoleManager;
    }

    @Override
    public void run() {

        try (
            BufferedReader reader =
                new BufferedReader(
                    new InputStreamReader(inputStream)
                )
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                consoleManager.append(
                    applicationId,
                    line
                );

                System.out.printf(
                    "[%s] %s%n",
                    applicationId,
                    line
                );
            }

        } catch (Exception exception) {

            exception.printStackTrace();

        }
    }
}