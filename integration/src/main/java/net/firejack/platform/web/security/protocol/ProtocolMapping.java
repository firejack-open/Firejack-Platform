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

package net.firejack.platform.web.security.protocol;

import net.firejack.platform.core.model.registry.EntityProtocol;


public class ProtocolMapping implements Comparable<ProtocolMapping> {

    private EntityProtocol protocol;
    private String urlPrefix;

    /**
     * @return
     */
    public EntityProtocol getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     */
    public void setProtocol(EntityProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @return
     */
    public String getUrlPrefix() {
        return urlPrefix;
    }

    /**
     * @param urlPrefix
     */
    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    @Override
    public int compareTo(ProtocolMapping mapping) {
        int result;
        if (mapping == null) {
            result = 1;
        } else if (mapping == this) {
            result = 0;
        } else if (getUrlPrefix() != null) {
            result = -getUrlPrefix().compareTo(mapping.getUrlPrefix());
        } else {
            result = 0;
        }
        return result;
    }
}