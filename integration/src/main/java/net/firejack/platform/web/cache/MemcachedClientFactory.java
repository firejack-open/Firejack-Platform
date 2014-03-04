/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.web.cache;

import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.utils.StringUtils;
import net.spy.memcached.MemcachedClient;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


public class MemcachedClientFactory extends BasePoolableObjectFactory<MemcachedClient> {

    private String memcachedServerUrl;
    private Integer port;
    private static final Logger logger = Logger.getLogger(MemcachedClientFactory.class);

    public MemcachedClientFactory(String memcachedServerUrl, Integer port) {
        this.memcachedServerUrl = memcachedServerUrl;
        this.port = port;
    }

    @Override
    public MemcachedClient makeObject() throws Exception {
        MemcachedClient client;
        if (StringUtils.isNotBlank(memcachedServerUrl) && port != null) {
            logger.info("Adding new memcached client to pool.");
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(memcachedServerUrl, port);
                if (inetSocketAddress.isUnresolved()) {
                    client = null;
                    logger.error("Memcached server hostname [" + memcachedServerUrl + "] is unresolved.");
                } else {
                    List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
                    addresses.add(inetSocketAddress);

                    client = new MemcachedClient(new OpenFlameMemcachedConnectionFactory(), addresses);
                }
            } catch (IOException e) {
                logger.error("Application failed to obtain connection to memcached server.");
                throw new OpenFlameRuntimeException(e.getMessage(), e);
            }
        } else {
            logger.warn("Memcached server url or port is not set.");
            client = null;
        }

        return client;
    }

    @Override
    public void destroyObject(MemcachedClient mcClient) throws Exception {
        logger.debug("Destroying memcached client.");
        if (mcClient != null) {
            mcClient.shutdown();
        }
        super.destroyObject(mcClient);
    }

}