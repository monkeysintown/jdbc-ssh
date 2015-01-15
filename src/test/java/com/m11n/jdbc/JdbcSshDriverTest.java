package com.m11n.jdbc;

import com.m11n.jdbc.util.BogusPasswordAuthenticator;
import com.m11n.jdbc.util.TestCachingPublicKeyAuthenticator;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.command.UnknownCommand;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;

import static com.m11n.jdbc.JdbcSshConfiguration.CONFIG_HOST;
import static com.m11n.jdbc.JdbcSshConfiguration.CONFIG_PORT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class JdbcSshDriverTest {
    private static final Logger logger = LoggerFactory.getLogger(JdbcSshDriverTest.class);

    protected String sshUrl;

    protected String realUrl;

    protected static SshServer sshd;

    protected String sql;

    protected static void setUpSshd() throws Exception {
        if(sshd==null && "true".equals(System.getProperty("sshd"))) {
            Properties p = new Properties();
            p.load(JdbcSshDriver.class.getClassLoader().getResourceAsStream("ssh.properties"));

            sshd = SshServer.setUpDefaultServer();
            sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("target/hostkey.rsa", "RSA"));
            sshd.setPasswordAuthenticator(new BogusPasswordAuthenticator());
            sshd.setPublickeyAuthenticator(new TestCachingPublicKeyAuthenticator());
            sshd.setCommandFactory(new CommandFactory() {
                public Command createCommand(String command) {
                    return new UnknownCommand(command);
                }
            });
            sshd.setHost(p.getProperty(CONFIG_HOST));
            sshd.setPort(Integer.valueOf(p.getProperty(CONFIG_PORT)));
            sshd.start();
        }
    }

    protected static void shutDownSshd() throws Exception {
        if(sshd!=null && !sshd.isClosed()) {
            sshd.stop();
        }
    }

    @Test
    public void testDriverRegistration() throws SQLException {
        boolean found = false;

        for(Enumeration<Driver> drivers = DriverManager.getDrivers(); drivers.hasMoreElements();) {
            Driver driver = drivers.nextElement();

            if(driver.getClass().equals(JdbcSshDriver.class)) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }

    @Test
    public void testSshDriver() throws Exception {
        Connection connection = DriverManager.getConnection(sshUrl);

        logger.info("Info: {}", connection.getClientInfo());

        DatabaseMetaData metadata = connection.getMetaData();

        // Get all the tables and views
        String[] tableType = {"TABLE"};
        java.sql.ResultSet tables = metadata.getTables(null, null, "%", tableType);

        assertNotNull(tables);

        String tableName;
        while (tables.next()) {
            tableName = tables.getString(3);

            logger.info("Table: {}", tableName);
        }
    }

    @Test
    public void testRealDriver() throws Exception {
        Connection connection = DriverManager.getConnection(realUrl);

        Statement s = connection.createStatement();
        s.execute(sql);

        DatabaseMetaData metadata = connection.getMetaData();

        // Get all the tables and views
        String[] tableType = {"TABLE"};
        java.sql.ResultSet tables = metadata.getTables(null, null, "%", tableType);

        assertNotNull(tables);

        String tableName;
        while (tables.next()) {
            tableName = tables.getString(3);

            logger.info("Table: {}", tableName);
        }
    }
}
