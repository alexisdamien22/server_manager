package application;

import com.alexisdamien22.servermanager.application.ApplicationLauncher;
import com.alexisdamien22.servermanager.config.ApplicationConfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationLauncherTest {

    @Test
    void shouldLaunchProcess()
            throws Exception {

        ApplicationConfig config =
                new ApplicationConfig();

        config.setId("test");
        config.setName("Test");
        config.setType("utility");
        config.setWorkingDirectory("/tmp");
        config.setStartupCommand(
                java.util.List.of(
                        "sleep",
                        "5"
                )
        );

        ApplicationLauncher launcher =
                new ApplicationLauncher();

        Process process =
                launcher.launch(config);

        assertTrue(process.isAlive());

        process.destroyForcibly();
    }
}