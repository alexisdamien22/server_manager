package com.alexisdamien22.servermanager.console;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConsoleReader
        implements Runnable {

    private final String applicationId;

    private final InputStream stream;

    public ConsoleReader(
            String applicationId,
            InputStream stream) {

        this.applicationId = applicationId;
        this.stream = stream;
    }

    @Override
    public void run() {

        try (
            BufferedReader reader =
                new BufferedReader(
                    new InputStreamReader(stream)
                )
        ) {

            String line;

            while ((line = reader.readLine())
                    != null) {

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