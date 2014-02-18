package net.firejack.platform.generate.beans.web.api;

import net.firejack.platform.generate.beans.web.broker.Broker;
import net.firejack.platform.generate.beans.web.store.Method;

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

public class LocalMethod extends Method {

    private Broker broker;
    private String name;
    private String soapPath;
    private String path;

    /**
     * @param broker
     */
    public LocalMethod(Broker broker) {
        this.broker = broker;
        this.type = broker.getType();
    }

    /**
     * @return
     */
    public Broker getBroker() {
        return broker;
    }

    /**
     * @param broker
     */
    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public String getSoapPath() {
        return soapPath;
    }

    /**
     * @param soapPath
     */
    public void setSoapPath(String soapPath) {
        this.soapPath = soapPath;
    }

    /**
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
