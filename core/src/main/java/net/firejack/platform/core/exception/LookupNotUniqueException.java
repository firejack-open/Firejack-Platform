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

package net.firejack.platform.core.exception;


public class LookupNotUniqueException extends BusinessFunctionException {
    private static final long serialVersionUID = -1773433313044963978L;

    private String type;
    private String lookup;

    /**
     * @param type
     * @param lookup
     * @param msgKey
     * @param messageArguments
     */
    public LookupNotUniqueException(String type, String lookup, String msgKey, Object... messageArguments) {
        super(msgKey, messageArguments);
        this.type = type;
        this.lookup = lookup;
    }

    /**
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return
     */
    public String getLookup() {
        return lookup;
    }

    /**
     * @param lookup
     */
    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

}
