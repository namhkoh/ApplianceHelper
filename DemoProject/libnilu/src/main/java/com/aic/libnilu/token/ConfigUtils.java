package com.aic.libnilu.token;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConfigUtils {
    public static Config loadConfig(String configPath) {
        //System.out.println("Load " + configPath);

        Config config;
        final ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream json = new FileInputStream(configPath);
            config = mapper.readValue(json, new TypeReference<Config>() {
            });

            return config;
        } catch (IOException e) {
            //System.out.println("Error to load");
            e.printStackTrace();
            return null;
        }
    }
}
