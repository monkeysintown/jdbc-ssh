package com.m11n.jdbc.ssh;

public final class Constants {
    public static final String CONFIG = "jdbc.ssh.config";
    public static final String DRIVER_PREFIX = "jdbc:ssh:";
    public static final String CONFIG_HOST = "jdbc.ssh.host";
    public static final String CONFIG_PORT = "jdbc.ssh.port";
    public static final String CONFIG_USERNAME = "jdbc.ssh.username";
    public static final String CONFIG_PASSWORD = "jdbc.ssh.password";
    public static final String CONFIG_HOST_REMOTE = "jdbc.ssh.host.remote";
    public static final String CONFIG_PORT_REMOTE = "jdbc.ssh.port.remote";
    public static final String CONFIG_PORT_AUTO = "jdbc.ssh.port.auto";
    public static final String[] CONFIG_ALL = {CONFIG_PORT_AUTO, CONFIG_HOST, CONFIG_HOST_REMOTE, CONFIG_PASSWORD, CONFIG_PORT, CONFIG_PORT_REMOTE, CONFIG_USERNAME};

    private Constants() {
    }
}
