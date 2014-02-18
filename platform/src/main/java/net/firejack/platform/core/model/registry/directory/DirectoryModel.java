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

package net.firejack.platform.core.model.registry.directory;

import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.model.registry.FieldContainerRegistryNode;
import net.firejack.platform.core.model.registry.IAllowCreateAutoDescription;
import net.firejack.platform.core.model.registry.ISortable;
import net.firejack.platform.core.model.registry.RegistryNodeType;

import javax.persistence.*;


@javax.persistence.Entity
@DiscriminatorValue("DIR")
public class DirectoryModel extends FieldContainerRegistryNode implements ISortable, IAllowCreateAutoDescription {

    private static final long serialVersionUID = 271784164368960545L;
    private DirectoryType directoryType;
    private String serverName;
    private Integer port;
    private String urlPath;
    private String baseDN;
    private String rootDN;
    private String password;
    private String ldapSchemaConfig;
    private RegistryNodeStatus status;

    /**
     * @return
     */
    @Enumerated
    public DirectoryType getDirectoryType() {
        return directoryType;
    }

    /**
     * @param directoryType
     */
    public void setDirectoryType(DirectoryType directoryType) {
        this.directoryType = directoryType;
    }

    /**
     * @return
     */
    @Column(length = 1024)
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Column
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return
     */
    @Column(length = 2048)
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * @param urlPath
     */
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    /**
     * @return
     */
    @Enumerated
    public RegistryNodeStatus getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(RegistryNodeStatus status) {
        this.status = status;
    }

    @Column(name="base_dn", length = 255)
    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    @Column(name="root_dn", length = 255)
    public String getRootDN() {
        return rootDN;
    }

    public void setRootDN(String rootDN) {
        this.rootDN = rootDN;
    }

    @Column(length = 64)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name="ldap_schema_config", length = 2048)
    public String getLdapSchemaConfig() {
        return ldapSchemaConfig;
    }

    public void setLdapSchemaConfig(String ldapSchemaConfig) {
        this.ldapSchemaConfig = ldapSchemaConfig;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.DIRECTORY;
    }

}