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

package net.firejack.platform.core.model.registry.site;

import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.List;


@javax.persistence.Entity
@DiscriminatorValue("NAV")
public class NavigationElementModel extends RegistryNodeModel implements ISortable, INavigableRegistryNode, IAllowDrag, IAllowDrop, IFolder, IAllowCreateAutoDescription {

    private static final long serialVersionUID = -1209284431447149479L;
    private String serverName;
    private String parentPath;
    private String urlPath;
    private String urlParams;
    private Integer port;
    private EntityProtocol protocol;
    private RegistryNodeStatus status;
    private String pageUrl;
    private Boolean hidden;
    private NavigationElementType elementType;

    private List<PermissionModel> permissions;

    @Column(length = 1024)
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Column(length = 255)
    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    @Column(length = 2048)
    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    @Column(length = 2048)
    public String getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(String urlParams) {
        this.urlParams = urlParams;
    }

    /**
     * @return
     */
    @Enumerated
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

    @ManyToMany(targetEntity = PermissionModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_navigation_element_permission",
            joinColumns = @JoinColumn(name = "id_navigation_element", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "id_permission", referencedColumnName = "ID")
    )
    @ForeignKey(name = "FK_NAV_ELEMENT_PERMISSIONS")
    public List<PermissionModel> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionModel> permissions) {
        this.permissions = permissions;
    }

    @Column(length = 2048)
    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    @Column(name = "hidden")
    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @Enumerated
    @Column(name = "element_type")
    public NavigationElementType getElementType() {
        return elementType;
    }

    public void setElementType(NavigationElementType elementType) {
        this.elementType = elementType;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.NAVIGATION_ELEMENT;
    }

}
