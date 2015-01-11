package com.m11n.jdbc.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static com.m11n.jdbc.ssh.Constants.*;

public class JdbcSshConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JdbcSshConfiguration.class);

    private Properties config;

    public JdbcSshConfiguration() {
        config = new Properties();

        try {
            InputStream is;

            String path = System.getProperty(CONFIG)==null ? "ssh.properties" : System.getProperty(CONFIG);

            File f = new File(path);

            if(f.exists()) {
                is = new FileInputStream(f);
            } else {
                is = JdbcSshConfiguration.class.getClassLoader().getResourceAsStream(path);
            }

            if(is!=null) {
                config.load(is);
            }

            config = setDefaults(config);
        } catch(Exception e) {
            logger.warn(e.toString(), e);
        }
    }

    public JdbcSshConfiguration(Properties c) {
        this();

        // NOTE: assume same username and password for SSH connection and database if not set explicitly
        if(c.getProperty("user")!=null && c.getProperty(CONFIG_USERNAME) == null) {
            config.setProperty(CONFIG_USERNAME, c.getProperty("user"));
        }
        if(c.getProperty("password")!=null && c.getProperty(CONFIG_PASSWORD) == null) {
            config.setProperty(CONFIG_PASSWORD, c.getProperty("password"));
        }

        for(String key : CONFIG_ALL) {
            if(c.getProperty(key)!=null && !"".equals(c.getProperty(key).trim())) {
                config.setProperty(key, c.getProperty(key));
            }
        }
    }

    private Properties setDefaults(Properties c) {
        if(c.getProperty("Compression")==null) {
            c.setProperty("Compression", "no");
        }
        if(c.getProperty("ConnectionAttempts")==null) {
            c.setProperty("ConnectionAttempts", "2");
        }
        if(c.getProperty("StrictHostKeyChecking")==null) {
            c.put("StrictHostKeyChecking", "no");
        }
        if(c.getProperty(CONFIG_PORT)==null) {
            c.put(CONFIG_PORT, "22");
        }
        if(c.getProperty(CONFIG_PORT_AUTO)==null) {
            c.put(CONFIG_PORT_AUTO, "20000");
        }

        return c;
    }

    public Properties getProperties() {
        return config;
    }

    public String getProperty(String name) {
        return config.getProperty(name);
    }
}
