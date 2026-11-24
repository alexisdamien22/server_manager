package server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.alexisdamien22.servermanager.config.ServerConfig;
import com.alexisdamien22.servermanager.server.ManagedServer;
import com.alexisdamien22.servermanager.server.ServerManager;

public class ServerManagerTest {
    @Test
    void shouldRegisterRunningServer() {
        ServerConfig config = new ServerConfig();

        config.setId("survival");
        config.setName("Survival SMP");
        config.setWorkingDirectory("./servers/survival");
        config.setStartupCommand("java -jar paper.jar nogui");

        ManagedServer server =
                new ManagedServer(config, null);

        ServerManager manager =
                new ServerManager();

        manager.register(server);

        assertTrue(
            manager.isRunning(server.getId())
        );
}
}
