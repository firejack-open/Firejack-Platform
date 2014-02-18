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

package net.firejack.platform.core.model.registry;


public enum RESTMethod {

    CREATE(HTTPMethod.POST),

    READ(HTTPMethod.GET),

    READ_ALL(HTTPMethod.GET, "read-all"),

    UPDATE(HTTPMethod.PUT),

    DELETE(HTTPMethod.DELETE),

    SEARCH(HTTPMethod.GET),

    ADVANCED_SEARCH(HTTPMethod.GET, "advanced-search");

    private HTTPMethod method;
    private String actionName;

    RESTMethod(HTTPMethod method) {
        this.method = method;
        this.actionName = name().toLowerCase();
    }

    RESTMethod(HTTPMethod method, String actionName) {
        this.method = method;
        this.actionName = actionName;
    }

    /**
     * @return
     */
    public HTTPMethod getMethod() {
        return method;
    }

    /**
     * @param method
     */
    public void setMethod(HTTPMethod method) {
        this.method = method;
    }

    /**
     * @return
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @param name
     * @return
     */
    public static RESTMethod findByName(String name) {
        RESTMethod value = null;
        for (RESTMethod e : values()) {
            if (e.name().equals(name)) {
                value = e;
                break;
            }
        }
        return value;
    }

}
