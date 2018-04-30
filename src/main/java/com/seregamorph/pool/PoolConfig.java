package com.seregamorph.pool;

import java.util.Properties;

public class PoolConfig {
    final String url;
    final Properties properties;
    final int maxSize;

    // todo Builder
    public PoolConfig(String url, Properties properties, int maxSize) {
        this.url = url;
        this.properties = properties;
        this.maxSize = maxSize;
    }
}
