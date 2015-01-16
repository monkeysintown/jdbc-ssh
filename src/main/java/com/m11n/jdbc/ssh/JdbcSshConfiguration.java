package com.m11n.jdbc.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * = JdbcSshConfiguration
 *
 * A think wrapper around `java.util.Properties`.
 *
 * @author https://github.com/vidakovic[Aleksandar Vidakovic]
 */
public class JdbcSshConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JdbcSshConfiguration.class);

    public static final String CONFIG = "jdbc.ssh.config";
    public static final String DRIVER_PREFIX = "jdbc:ssh:";
    public static final String CONFIG_HOST = "jdbc.ssh.host";
    public static final String CONFIG_PORT = "jdbc.ssh.port";
    public static final String CONFIG_USERNAME = "jdbc.ssh.username";
    public static final String CONFIG_PASSWORD = "jdbc.ssh.password";
    public static final String CONFIG_KEY_PRIVATE = "jdbc.ssh.key.private";
    public static final String CONFIG_KEY_PUBLIC = "jdbc.ssh.key.public";
    public static final String CONFIG_PASSPHRASE = "jdbc.ssh.passphrase";
    public static final String CONFIG_KNOWN_HOSTS = "jdbc.ssh.known.hosts";
    public static final String CONFIG_HOST_REMOTE = "jdbc.ssh.host.remote";
    public static final String CONFIG_PORT_REMOTE = "jdbc.ssh.port.remote";
    public static final String CONFIG_PORT_AUTO = "jdbc.ssh.port.auto";

    private Properties config;

    private final Set<String> allowed = new HashSet<>();

    public JdbcSshConfiguration() {
        importAllowedPropertyNames();

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

            check(config);

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

        for(String key : allowed) {
            if(c.getProperty(key)!=null && !"".equals(c.getProperty(key).trim())) {
                config.setProperty(key, c.getProperty(key));
            }
        }

        if(logger.isDebugEnabled()) {
            logger.debug("Configuration: {}", config);
        }
    }

    private void importAllowedPropertyNames() {
        try {
            allowed.clear();

            BufferedReader reader = new BufferedReader(new InputStreamReader(JdbcSshConfiguration.class.getClassLoader().getResourceAsStream("PROPERTIES")));

            String line;

            while((line=reader.readLine())!=null) {
                allowed.add(line);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Properties setDefaults(Properties c) {
        if(c.getProperty("Compression")==null) {
            c.put("Compression", getSystemPropertyOrDefault("Compression", "no"));
        }
        if(c.getProperty("ConnectionAttempts") == null) {
            c.put("ConnectionAttempts", getSystemPropertyOrDefault("ConnectionAttempts", "2"));
        }
        if(c.getProperty("StrictHostKeyChecking")==null) {
            c.put("StrictHostKeyChecking", getSystemPropertyOrDefault("StrictHostKeyChecking", "no"));
        }
        if(c.getProperty(CONFIG_HOST)==null) {
            c.put(CONFIG_HOST, getSystemPropertyOrDefault(CONFIG_HOST, "localhost"));
        }
        if(c.getProperty(CONFIG_PORT)==null) {
            c.put(CONFIG_PORT, getSystemPropertyOrDefault(CONFIG_PORT, "22"));
        }
        if(c.getProperty(CONFIG_PORT_AUTO)==null) {
            c.put(CONFIG_PORT_AUTO, getSystemPropertyOrDefault(CONFIG_PORT_AUTO, "20000"));
        }
        if(c.getProperty(CONFIG_USERNAME)==null) {
            c.put(CONFIG_USERNAME, System.getProperty(CONFIG_USERNAME));
        }
        if(c.getProperty(CONFIG_PASSWORD)==null) {
            c.put(CONFIG_PASSWORD, System.getProperty(CONFIG_PASSWORD));
        }
        if(c.getProperty(CONFIG_KEY_PRIVATE)==null) {
            c.put(CONFIG_KEY_PRIVATE, getSystemPropertyOrDefault(CONFIG_KEY_PRIVATE, ""));
        }
        if(c.getProperty(CONFIG_KEY_PUBLIC)==null) {
            c.put(CONFIG_KEY_PUBLIC, getSystemPropertyOrDefault(CONFIG_KEY_PUBLIC, c.getProperty(CONFIG_KEY_PRIVATE) + ".pub"));
        }
        if(c.getProperty(CONFIG_PASSPHRASE)==null) {
            c.put(CONFIG_PASSPHRASE, getSystemPropertyOrDefault(CONFIG_PASSPHRASE, ""));
        }
        if(c.getProperty(CONFIG_KNOWN_HOSTS)==null) {
            c.put(CONFIG_KNOWN_HOSTS, getSystemPropertyOrDefault(CONFIG_KNOWN_HOSTS, "~/.ssh/known_hosts"));
        }

        return c;
    }

    private void check(Properties c) {
        Enumeration<Object> keys = c.keys();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            if(!allowed.contains(key)) {
                logger.warn("Skipping property: {}", key);
                c.remove(key);
            }
        }
    }

    private String getSystemPropertyOrDefault(String name, String defaultValue) {
        return System.getProperty(name)==null ? defaultValue : System.getProperty(name);
    }

    public Properties getProperties() {
        return config;
    }

    public String getProperty(String name) {
        return config.getProperty(name);
    }
}
