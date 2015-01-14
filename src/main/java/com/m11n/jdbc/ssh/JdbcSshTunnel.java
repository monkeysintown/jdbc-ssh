package com.m11n.jdbc.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import static com.m11n.jdbc.ssh.Constants.*;

public class JdbcSshTunnel {
    private static final Logger logger = LoggerFactory.getLogger(JdbcSshTunnel.class);

    private JdbcSshConfiguration config;

    private Session session;

    private AtomicInteger localPort;

    public JdbcSshTunnel(JdbcSshConfiguration config) {
        this.config = config;

        localPort = new AtomicInteger(Integer.valueOf(config.getProperty(CONFIG_PORT_AUTO)));

        logger.info("Automatic local port assignment starts at: {}", localPort.get());

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
            String key = config.getProperty(CONFIG_KEY);
            String host = config.getProperty(CONFIG_HOST);
            Integer port = Integer.valueOf(config.getProperty(CONFIG_PORT));

            assert host!=null;
            assert port!=null;

            boolean useKey = (key!=null && !"".equals(key.trim()));

            if(useKey) {
                jsch.addIdentity(key);
            }

            session = jsch.getSession(username, host, port);

            if(!useKey) {
                session.setPassword(password);
            }

            session.setConfig(config.getProperties());
            session.setDaemonThread(true);

            // Connect
            session.connect();

            Channel channel = session.openChannel("shell");
            channel.connect();

            String forwardHost = config.getProperty(CONFIG_HOST_REMOTE);
            Integer remotePort = Integer.valueOf(config.getProperty(CONFIG_PORT_REMOTE));

            int nextPort = localPort.incrementAndGet();

            // NOTE: scan max next 10 ports
            for(int i=0; i<10; i++) {
                if(isPortOpen("127.0.0.1", nextPort)) {
                    break;
                }

                nextPort = localPort.incrementAndGet();
            }

            assignedPort = session.setPortForwardingL(localPort.incrementAndGet(), forwardHost, remotePort);

            if(logger.isDebugEnabled()) {
                logger.debug("Server version: {}", session.getServerVersion());
                logger.debug("Client version: {}", session.getClientVersion());
                logger.debug("Host          : {}", session.getHost());
                logger.debug("Port          : {}", session.getPort());
                logger.debug("Forwarding    : {}", session.getPortForwardingL());
                logger.debug("Connected     : {}", session.isConnected());
                logger.debug("Private key   : {}", useKey);
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

    public Integer getLocalPort() {
        return localPort.get();
    }

    public boolean isPortOpen(String ip, int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 1000);
            socket.close();
            return false;
        } catch (Exception ex) {
            return true;
        }
    }
}
