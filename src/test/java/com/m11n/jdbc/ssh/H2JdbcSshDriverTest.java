package com.m11n.jdbc.ssh;

import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2JdbcSshDriverTest extends JdbcSshDriverTest {
    private static final Logger logger = LoggerFactory.getLogger(H2JdbcSshDriverTest.class);

    private static Server dbServerH2;

    @BeforeClass
    public static void init() throws Exception {
        setUpH2();
        setUpSshd();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        dbServerH2.shutdown();
        shutDownSshd();
    }

    @Before
    public void setUp() throws Exception {
        sshUrl = System.getProperty("url")!=null ? System.getProperty("url") : "jdbc:ssh:h2:" + dbServerH2.getURL() + "/test";
        realUrl = System.getProperty("realUrl")!=null ? System.getProperty("realUrl") : "jdbc:h2:" + dbServerH2.getURL() + "/test";

        sql = "CREATE TABLE IF NOT EXISTS TEST_SSH(ID INT PRIMARY KEY, NAME VARCHAR(255));";

        logger.info("JDBC URL (SSH) : {}", sshUrl);
        logger.info("JDBC URL (real): {}", realUrl);
    }

    private static void setUpH2() throws Exception {
        System.setProperty("h2.baseDir", "./target/h2");
        System.setProperty("jdbc.ssh.port.auto", "20000");

        dbServerH2 = Server.createTcpServer("-tcpPort" , "8092" , "-tcpAllowOthers", "-tcpDaemon").start();

        logger.info("Database server status: u = {} - s = {} ({})", dbServerH2.getURL(), dbServerH2.getStatus(), dbServerH2.isRunning(true));
    }
}
