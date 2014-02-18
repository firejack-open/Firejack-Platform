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

package net.firejack.platform.core.model.registry.authority;

import net.firejack.platform.core.model.registry.INavigableRegistryNode;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("LOC")
public class ResourceLocationModel extends RegistryNodeModel implements INavigableRegistryNode {

    private static final long serialVersionUID = 6251256905661202554L;
    private String serverName;
    private Integer port;
    private String parentPath;
    private String urlPath;
    private WildcardStyle wildcardStyle;

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

    /**
     * @return
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "wildcard_style")
    public WildcardStyle getWildcardStyle() {
        return wildcardStyle;
    }

    /**
     * @param wildcardStyle
     */
    public void setWildcardStyle(WildcardStyle wildcardStyle) {
        this.wildcardStyle = wildcardStyle;
    }

    @ManyToMany(targetEntity = PermissionModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_resource_location_permission",
            joinColumns = @JoinColumn(name = "id_resource_location", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "id_permission", referencedColumnName = "ID")
    )
    @ForeignKey(name = "FK_RESOURCE_LOCATION_PERMISSIONS")
    public List<PermissionModel> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionModel> permissions) {
        this.permissions = permissions;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.RESOURCE_LOCATION;
    }

    @Override
    @Transient
    public boolean isDisplayedInTree() {
        return false;
    }

}