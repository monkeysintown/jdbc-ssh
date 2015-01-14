package com.m11n.jdbc.ssh;

import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2JdbcSshDriverTest extends JdbcSshDriverTest {
    private static final Logger logger = LoggerFactory.getLogger(H2JdbcSshDriverTest.class);

    private Server dbServerH2;

    @Before
    public void setUp() throws Exception {
        setUpH2();
        setUpSshd();
    }

    private void setUpH2() throws Exception {
        System.setProperty("h2.baseDir", "./target/h2");

        dbServerH2 = Server.createTcpServer("-tcpPort" , "8092" , "-tcpAllowOthers", "-tcpDaemon", "-trace").start();

        logger.info("Database server status: u = {} - s = {} ({})", dbServerH2.getURL(), dbServerH2.getStatus(), dbServerH2.isRunning(true));

        sshUrl = System.getProperty("url")!=null ? System.getProperty("url") : "jdbc:ssh:h2:" + dbServerH2.getURL() + "/test";
        realUrl = System.getProperty("realUrl")!=null ? System.getProperty("realUrl") : "jdbc:h2:" + dbServerH2.getURL() + "/test";

        sql = "CREATE TABLE IF NOT EXISTS TEST_SSH(ID INT PRIMARY KEY, NAME VARCHAR(255));";

        logger.info("JDBC URL (SSH) : {}", sshUrl);
        logger.info("JDBC URL (real): {}", realUrl);
    }

    @After
    public void shutdown() throws Exception {
        dbServerH2.stop();
        sshd.stop();
    }
}
