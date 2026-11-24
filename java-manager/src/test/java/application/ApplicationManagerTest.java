package application;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.alexisdamien22.servermanager.application.ApplicationManager;
import com.alexisdamien22.servermanager.config.ApplicationConfig;

public class ApplicationManagerTest {
    @Test
        void shouldStartApplication()
                throws Exception {

        ApplicationConfig config =
                new ApplicationConfig();

        config.setId("test");
        config.setName("Test");

        config.setWorkingDirectory("/tmp");

        config.setStartupCommand(
                List.of("sleep", "5")
        );

        ApplicationManager manager =
                new ApplicationManager();

        manager.startApplication(config);

        assertTrue(
                manager.isRunning("test")
        );

        manager.stopApplication("test");
        }
}
