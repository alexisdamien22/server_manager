package com.alexisdamien22.servermanager.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleReader
        implements Runnable {

    private final Process process;

    public ConsoleReader(Process process) {
        this.process = process;
    }

    @Override
    public void run() {

        try (BufferedReader reader =
                new BufferedReader(
                    new InputStreamReader(
                        process.getInputStream()
                    ))) {

            String line;

            while ((line = reader.readLine()) != null) {

                System.out.println(
                    "[SERVER] " + line
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}