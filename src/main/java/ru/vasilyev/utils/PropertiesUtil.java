package ru.vasilyev.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Properties PROPERTIES = loadProperties();

    private PropertiesUtil() {
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream resourceAsStream =
                     PropertiesUtil.class
                             .getClassLoader()
                             .getResourceAsStream("application.properties")
        ) {
            if (resourceAsStream == null) {
                throw new RuntimeException("Application properties file not found");
            }
            properties.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
}
