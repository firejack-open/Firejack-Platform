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


public enum RegistryNodeProtocol {

    HTTP("http://", 80),

    HTTPS("https://", 443),
    JDBC("jdbc://", 3306);

    private String protocol;
    private Integer port;

    RegistryNodeProtocol(String protocol, Integer port) {
        this.protocol = protocol;
        this.port = port;
    }

    /**
     * @return
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @param name
     * @return
     */
    public static RegistryNodeProtocol findByName(String name) {
        RegistryNodeProtocol value = null;
        for (RegistryNodeProtocol e : values()) {
            if (e.name().equals(name)) {
                value = e;
                break;
            }
        }
        return value;
    }

}
