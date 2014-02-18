package net.firejack.platform.generate.beans.web.store;

import java.util.TreeSet;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

public class Method {
    protected MethodType type;
    protected TreeSet<Param> params;
    protected Param returnType;
    protected String description;

    /***/
    public Method() {
    }

    /**
     * @param type
     */
    public Method(MethodType type) {
        this.type = type;
    }

    /**
     * @param method
     */
    public Method(Method method) {
        this.type = method.getType();
        this.params = method.getParams();
        this.returnType = method.getReturnType();
        this.description = method.getDescription();
    }

    /**
     * @return
     */
    public MethodType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(MethodType type) {
        this.type = type;
    }

    /**
     * @return
     */
    public TreeSet<Param> getParams() {
        return params;
    }

    /**
     * @param param
     */
    public void addParam(Param param) {
        if (this.params == null) {
            this.params = new TreeSet<Param>();
        }
        this.params.add(param);
    }

    /**
     * @return
     */
    public int getNextOrder() {
        if (params != null) {
            return params.size() + 1;
        } else {
            return 0;
        }
    }

    /**
     * @return
     */
    public Param getReturnType() {
        return returnType;
    }

    /**
     * @param returnType
     */
    public void setReturnType(Param returnType) {
        this.returnType = returnType;
    }

    /**
     * @return
     */
    public boolean isRender() {
        return params != null || returnType != null;
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
