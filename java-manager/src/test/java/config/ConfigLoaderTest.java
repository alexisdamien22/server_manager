package config;

import com.alexisdamien22.servermanager.config.ConfigLoader;
import com.alexisdamien22.servermanager.config.ManagerConfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigLoaderTest {
    @Test
    void shouldLoadServers() {
        ConfigLoader loader = new ConfigLoader();

        try {
            ManagerConfig config = loader.load();
            assertEquals(
                1,
                config.getServers().size()
            );

            assertEquals(
                "survival",
                config.getServers().get(0).getId()
            );
        } catch (Exception exception) {
            throw new AssertionError("Failed to load configuration", exception);
        }  
    }
}