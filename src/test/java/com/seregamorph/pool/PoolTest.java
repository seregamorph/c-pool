package com.seregamorph.pool;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class PoolTest {

    private Pool pool;

    @Before
    public void before() {
        Properties props = new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", "root");
        PoolConfig config = new PoolConfig("jdbc:mysql://localhost:3306/test", props, 2);

        this.pool = new Pool(config);
    }

    @After
    public void after() {
        this.pool.close();
    }

    @Test
    public void test() throws SQLException, InterruptedException {
        Conn firstConn;
        Connection firstConnection;

        try (Conn conn = pool.get()) {
            firstConn = conn;
            firstConnection = conn.getConnection();
            try (Statement st = firstConnection.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT id, value FROM test")) {
                    while (rs.next()) {
                        System.out.println(rs.getString("value"));
                    }
                }
            }
        }

        // todo complicated Pool state checks

        try (Conn conn = pool.get()) {
            Connection secondConnection = conn.getConnection();
            assertNotSame("Should not be the same Conn", firstConn, conn);
            assertSame("Should be the same Connection", firstConnection, secondConnection);
            try (Statement st = secondConnection.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT id, value FROM test")) {
                    while (rs.next()) {
                        System.out.println(rs.getString("value"));
                    }
                }
            }
        }
    }
}
