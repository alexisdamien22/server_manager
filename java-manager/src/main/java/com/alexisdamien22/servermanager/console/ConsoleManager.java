package com.alexisdamien22.servermanager.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsoleManager {

    private static final int MAX_MESSAGES = 1000;

    private final Map<String, List<ConsoleMessage>>
            buffers =
            new ConcurrentHashMap<>();

    public void append(
            String applicationId,
            String message) {

        List<ConsoleMessage> messages =
                buffers.computeIfAbsent(
                        applicationId,
                        key -> new ArrayList<>()
                );

        synchronized (messages) {

            messages.add(
                    new ConsoleMessage(
                            applicationId,
                            message
                    )
            );

            while (messages.size() > MAX_MESSAGES) {
                messages.remove(0);
            }
        }
    }

    public List<ConsoleMessage> getMessages(
            String applicationId) {

        List<ConsoleMessage> messages =
                buffers.get(applicationId);

        if (messages == null) {
            return List.of();
        }

        synchronized (messages) {
            return List.copyOf(messages);
        }
    }

    public void clear(
            String applicationId) {

        buffers.remove(applicationId);
    }

    public int getMessageCount(
            String applicationId) {

        List<ConsoleMessage> messages =
                buffers.get(applicationId);

        if (messages == null) {
            return 0;
        }

        return messages.size();
    }
}