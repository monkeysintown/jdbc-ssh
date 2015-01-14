package com.m11n.jdbc.ssh;

import org.apache.derby.drda.NetworkServerControl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.net.InetAddress;

public class DerbyJdbcSshDriverTest extends JdbcSshDriverTest {
    private static final Logger logger = LoggerFactory.getLogger(DerbyJdbcSshDriverTest.class);

    private static NetworkServerControl dbServerDerby;

    @BeforeClass
    public static void init() throws Exception {
        setUpDerby();
        setUpSshd();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        dbServerDerby.shutdown();
        shutDownSshd();
    }

    private static void setUpDerby() throws Exception {
        System.setProperty("derby.drda.startNetworkServer", "true");
        System.setProperty("jdbc.ssh.port.auto", "30000");

        dbServerDerby = new NetworkServerControl(InetAddress.getByName("localhost"),1527);
        dbServerDerby.start(new PrintWriter(System.out, true));

        for (int i = 0; i < 10; ++i) {
            try {
                logger.info("Attempting to ping...");
                dbServerDerby.ping();
                break;
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
            Thread.sleep(10);
        }
    }

    @Before
    public void setUp() throws Exception {
        sshUrl = System.getProperty("url")!=null ? System.getProperty("url") : "jdbc:ssh:derby://127.0.0.1:1527/target/test;create=true";
        realUrl = System.getProperty("realUrl")!=null ? System.getProperty("realUrl") : "jdbc:derby://127.0.0.1:1527/target/test;create=true";

        logger.info("JDBC URL (SSH) : {}", sshUrl);
        logger.info("JDBC URL (real): {}", realUrl);

        sql = "CREATE TABLE TEST_SSH(ID INT PRIMARY KEY, NAME VARCHAR(255))";

        logger.info("JDBC Runtime Info:\n{}", dbServerDerby.getRuntimeInfo());
    }
}
