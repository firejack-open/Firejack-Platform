package net.firejack.platform.web.cache;

import net.spy.memcached.MemcachedClient;
import org.apache.commons.pool.impl.StackObjectPool;


public class MemcachedClientPool extends StackObjectPool<MemcachedClient> {

    public MemcachedClientPool(MemcachedClientFactory factory) {
        super(factory, 4, 8);
    }

}