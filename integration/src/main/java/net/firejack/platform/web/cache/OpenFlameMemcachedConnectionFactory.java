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

import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.HashAlgorithm;

public class OpenFlameMemcachedConnectionFactory extends DefaultConnectionFactory {

    private static final long DEFAULT_OPERATION_TIMEOUT = 6000L;
    private Long connectionTimeout;

    /**
     * @param qLen
     * @param bufSize
     * @param hash
     */
    public OpenFlameMemcachedConnectionFactory(int qLen, int bufSize, HashAlgorithm hash) {
        super(qLen, bufSize, hash);
    }

    /**
     * @param qLen
     * @param bufSize
     */
    public OpenFlameMemcachedConnectionFactory(int qLen, int bufSize) {
        super(qLen, bufSize);
    }

    /***/
    public OpenFlameMemcachedConnectionFactory() {
        super();
    }

    @Override
    public long getOperationTimeout() {
        if (connectionTimeout == null) {
            connectionTimeout = DEFAULT_OPERATION_TIMEOUT;
        }
        return connectionTimeout;
    }
}