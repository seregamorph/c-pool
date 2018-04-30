package com.seregamorph.pool;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;

public class Conn implements AutoCloseable {

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private final Pool pool;
    final Connection connection;

    Conn(Pool pool, Connection connection) {
        this.pool = pool;
        this.connection = connection;
    }

    public Connection getConnection() {
        if (closed.get()) {
            // to avoid reusage of Conn object after close()
            throw new IllegalStateException("Already closed (returned to pool)");
        }
        return connection;
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            // todo explicit rollback/setAutoCommit to default
            pool.put(this);
        }
        // else no-op
    }
}
