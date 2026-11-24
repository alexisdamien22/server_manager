package server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.alexisdamien22.servermanager.server.ManagedServer;
import com.alexisdamien22.servermanager.server.ServerManager;

public class ServerManagerTest {
    @Test
    void shouldRegisterRunningServer() {
        ServerManager manager =
            new ServerManager();

        ManagedServer server =
            new ManagedServer();

        manager.register(server);

        assertTrue(
            manager.isRunning(server.getId())
        );
}
}
