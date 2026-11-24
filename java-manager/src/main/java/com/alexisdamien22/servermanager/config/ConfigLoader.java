package com.alexisdamien22.servermanager.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;

public class ConfigLoader {

    public ManagerConfig load()
            throws Exception {

        Yaml yaml = new Yaml();

        try (InputStream input =
                     new FileInputStream("config/servers.yml")) {

            return yaml.loadAs(
                    input,
                    ManagerConfig.class
            );
        }
    }
}