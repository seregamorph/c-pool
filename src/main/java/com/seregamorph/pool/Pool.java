package com.seregamorph.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Simplest pool
 *
 * NOTE: do not use in production
 */
public class Pool implements AutoCloseable {

    private final PoolConfig config;

    private final Object sync = new Object();
    private final LinkedList<Conn> stack = new LinkedList<>();
    private int given;

    public Pool(PoolConfig config) {
        this.config = config;
    }

    public Conn get() throws SQLException, InterruptedException {
        synchronized (sync) {
            for (; ; ) {
                Conn conn = this.stack.poll();
                if (conn != null) {
                    given++;
                    assert given <= config.maxSize;
                    return conn;
                }

                if (given < config.maxSize) {
                    // no Class.forName required since jdk 1.6
                    Connection connection = DriverManager.getConnection(config.url, config.properties);
                    conn = new Conn(this, connection);
                    given++;
                    return conn;
                }

                sync.wait();
            }
        }
    }

    void put(Conn conn) {
        synchronized (sync) {
            Conn newConn = new Conn(this, conn.connection);
            given--;
            assert given >= 0;
            stack.add(newConn);
            sync.notify();
        }
    }

    @Override
    public void close() {
        // todo pool close lifecycle
    }
}
