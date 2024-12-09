package com.aneesh.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SecretsHelper {

    private static final Properties properties = new Properties();

    static {

        try (InputStream input = SecretsHelper.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find secrets.properties");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Function
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
