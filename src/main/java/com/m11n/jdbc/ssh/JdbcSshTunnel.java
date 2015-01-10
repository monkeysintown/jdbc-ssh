package com.m11n.jdbc.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.m11n.jdbc.ssh.Constants.*;

public class JdbcSshTunnel {
    private static final Logger logger = LoggerFactory.getLogger(JdbcSshTunnel.class);

    private JdbcSshConfiguration config;

    private Session session;

    public JdbcSshTunnel(JdbcSshConfiguration config) {
        this.config = config;

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Shutting down tunnel...");
                JdbcSshTunnel.this.stop();
            }
        });
    }

    public void start() {
        int assignedPort = 0;

        try {
            JSch jsch = new JSch();

            String username = config.getProperty(CONFIG_USERNAME);
            String password = config.getProperty(CONFIG_PASSWORD);
            String host = config.getProperty(CONFIG_HOST);
            Integer port = Integer.valueOf(config.getProperty(CONFIG_PORT));

            // TODO: password-less login
            assert username!=null;
            assert password!=null;
            assert host!=null;

            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            session.setConfig(config.getProperties());
            session.setDaemonThread(true);

            // Connect
            session.connect();

            Channel channel = session.openChannel("shell");
            channel.connect();

            Integer localPort = Integer.valueOf(config.getProperty(CONFIG_PORT_LOCAL));
            String forwardHost = config.getProperty(CONFIG_HOST_FORWARD);
            Integer remotePort = Integer.valueOf(config.getProperty(CONFIG_PORT_REMOTE));

            assignedPort = session.setPortForwardingL(localPort, forwardHost, remotePort);

            if(logger.isDebugEnabled()) {
                logger.info("Server version: {}", session.getServerVersion());
                logger.info("Client version: {}", session.getClientVersion());
                logger.info("Host          : {}", session.getHost());
                logger.info("Port          : {}", session.getPort());
                logger.info("Forwarding    : {}", session.getPortForwardingL());
                logger.info("Connected     : {}", session.isConnected());
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }

        if (assignedPort == 0) {
            throw new RuntimeException("Port forwarding failed !");
        }
    }

    public void stop() {
        if(session!=null) {
            session.disconnect();

            if(logger.isDebugEnabled()) {
                logger.debug("Disconnected.");
            }
        }
    }
}
