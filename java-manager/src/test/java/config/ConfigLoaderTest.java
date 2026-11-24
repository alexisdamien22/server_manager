package config;

import com.alexisdamien22.servermanager.config.ApplicationConfig;
import com.alexisdamien22.servermanager.config.ConfigLoader;
import com.alexisdamien22.servermanager.config.ManagerConfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    @Test
    void shouldLoadConfiguration()
            throws Exception {

        ConfigLoader loader =
                new ConfigLoader();

        ManagerConfig config =
                loader.load();

        assertNotNull(config);

        assertNotNull(
                config.getApplications()
        );

        assertFalse(
                config.getApplications()
                        .isEmpty()
        );
    }

    @Test
    void shouldLoadFirstApplication()
            throws Exception {

        ConfigLoader loader =
                new ConfigLoader();

        ManagerConfig config =
                loader.load();

        ApplicationConfig application =
                config.getApplications()
                      .get(0);

        assertNotNull(
                application.getId()
        );

        assertNotNull(
                application.getName()
        );

        assertNotNull(
                application.getType()
        );

        assertNotNull(
                application.getWorkingDirectory()
        );

        assertNotNull(
                application.getStartupCommand()
        );
    }

    @Test
    void shouldLoadExpectedApplication()
            throws Exception {

        ConfigLoader loader =
                new ConfigLoader();

        ManagerConfig config =
                loader.load();

        ApplicationConfig app =
                config.getApplications()
                        .get(0);

        assertEquals(
                "survival",
                app.getId()
        );

        assertEquals(
                "minecraft",
                app.getType()
        );

        assertFalse(
                app.isAutoStart()
        );
    }
}