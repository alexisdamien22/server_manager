package com.alexisdamien22.servermanager.console;

import java.time.Instant;

public class ConsoleMessage {

    private final String applicationId;

    private final String message;

    private final Instant timestamp;

    public ConsoleMessage(
            String applicationId,
            String message) {

        this.applicationId = applicationId;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}