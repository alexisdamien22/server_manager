package com.alexisdamien22.servermanager.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

public class ConfigLoader {

    private static final String CONFIG_PATH =
            "config/servers.test.yml";

    public ManagerConfig load()
            throws IOException {

        Yaml yaml = new Yaml();

        try (InputStream input =
                    new FileInputStream(CONFIG_PATH)) {

            ManagerConfig config =
                    yaml.loadAs(
                            input,
                            ManagerConfig.class
                    );

            if (config == null) {
                throw new IllegalStateException(
                        "Configuration file is empty"
                );
            }

            return config;
        }
    }
}