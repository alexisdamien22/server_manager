package server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ServerLauncherTest {
    @Test
    void shouldStartProcess()
            throws Exception {

        Process process =
            new ProcessBuilder(
                "sleep",
                "5"
            ).start();

        assertTrue(
            process.isAlive()
        );

        process.destroyForcibly();
    }
}
