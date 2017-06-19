package edu.technopolis.homework.messenger.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    public static String URL = null;
    public static String DB_USER_NAME = null;
    public static String DB_USER_PASSWORD = null;
    public static String DRIVER_CLASS_NAME = null;

    public Config() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(new File("config.ini")));

        URL = props.getProperty("URL");
        DB_USER_NAME = props.getProperty("DB_USER_NAME");
        DB_USER_PASSWORD = props.getProperty("DB_USER_PASSWORD");
        DRIVER_CLASS_NAME = props.getProperty("DRIVER_CLASS_NAME");
    }

    public static String getURL() {
        return URL;
    }

    public static String getDbUserName() {
        return DB_USER_NAME;
    }

    public static String getDbUserPassword() {
        return DB_USER_PASSWORD;
    }

    public static String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }
}
