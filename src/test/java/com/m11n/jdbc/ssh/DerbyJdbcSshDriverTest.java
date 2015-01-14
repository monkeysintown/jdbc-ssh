package com.m11n.jdbc.ssh;

import org.apache.derby.drda.NetworkServerControl;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.net.InetAddress;

public class DerbyJdbcSshDriverTest extends JdbcSshDriverTest {
    private static final Logger logger = LoggerFactory.getLogger(DerbyJdbcSshDriverTest.class);

    private NetworkServerControl dbServerDerby;

    static {
        System.setProperty("h2.baseDir", "/tmp");
    }

    @Before
    public void setUp() throws Exception {
        setUpDerby();
        setUpSshd();
    }

    private void setUpDerby() throws Exception {
        System.setProperty("derby.drda.startNetworkServer", "true");

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

        sshUrl = System.getProperty("url")!=null ? System.getProperty("url") : "jdbc:ssh:derby://127.0.0.1:1527/target/test;create=true";
        realUrl = System.getProperty("realUrl")!=null ? System.getProperty("realUrl") : "jdbc:derby://127.0.0.1:1527/target/test;create=true";

        sql = "CREATE TABLE TEST_SSH(ID INT PRIMARY KEY, NAME VARCHAR(255))";

        logger.info("JDBC Runtime Info: {}", dbServerDerby.getRuntimeInfo());
    }

    @After
    public void shutdown() throws Exception {
        dbServerDerby.shutdown();
        sshd.stop();
    }
}
