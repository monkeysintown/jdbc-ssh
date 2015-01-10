package com.m11n.jdbc.ssh;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Enumeration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JdbcSshDriverTest {
    private static final Logger logger = LoggerFactory.getLogger(JdbcSshDriverTest.class);

    private String url;

    @Before
    public void setUp() {
        url = System.getProperty("url");
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
    public void testMetadata() throws SQLException {
        Connection connection = DriverManager.getConnection(url);

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
