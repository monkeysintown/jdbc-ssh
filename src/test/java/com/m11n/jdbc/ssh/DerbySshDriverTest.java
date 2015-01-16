package com.m11n.jdbc.ssh;

import com.m11n.jdbc.ssh.util.Slf4jDerbyBridge;
import com.m11n.jdbc.ssh.util.Slf4jOutputStream;
import org.apache.derby.drda.NetworkServerControl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.net.InetAddress;

public class DerbySshDriverTest extends JdbcSshDriverTest {
    private static final Logger logger = LoggerFactory.getLogger(DerbySshDriverTest.class);

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

        Slf4jDerbyBridge.setLogger(logger);
        System.setProperty("derby.stream.error.method", Slf4jDerbyBridge.class.getName() + ".bridge");

        if(logger.isTraceEnabled()) {
            // see here for more options: http://wiki.apache.org/db-derby/DebugPropertiesTmpl
            System.setProperty("derby.drda.logConnections", "true");
            System.setProperty("derby.language.logStatementText", "true");
            System.setProperty("derby.language.logQueryPlan", "true");
            System.setProperty("derby.locks.deadlockTrace", "true");
        }

        dbServerDerby = new NetworkServerControl(InetAddress.getByName("localhost"), 1527);
        dbServerDerby.start(new PrintWriter(new Slf4jOutputStream(logger), true));

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
