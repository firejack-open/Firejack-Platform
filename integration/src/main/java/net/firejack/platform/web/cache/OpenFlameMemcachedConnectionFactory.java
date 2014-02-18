/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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