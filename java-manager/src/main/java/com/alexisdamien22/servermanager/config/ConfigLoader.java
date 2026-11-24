package com.alexisdamien22.servermanager.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
    
public class ConfigLoader {

    public List<Map<String, Object>> loadServers() throws Exception {

        Yaml yaml = new Yaml();

        try (InputStream input =
                     new FileInputStream("config/servers.yml")) {

            Map<String, Object> data =
                    yaml.load(input);

            return (List<Map<String, Object>>)
                    data.get("servers");
        }
    }
}
