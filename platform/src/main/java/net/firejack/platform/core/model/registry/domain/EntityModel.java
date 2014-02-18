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

package net.firejack.platform.core.model.registry.domain;

import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;


@Entity
@DiscriminatorValue("ENT")
public class EntityModel extends FieldContainerRegistryNode implements IUrlPathContainer, IAllowDrag, IAllowDrop, IAllowCreateAutoDescription, INavigable, IEntity {

    private static final long serialVersionUID = 1461128417492144637L;
    private String serverName;
    private Integer port;
    private String parentPath;
    private String urlPath;
    private EntityProtocol protocol;
    private HTTPMethod method;
    private RegistryNodeStatus status;

    private Boolean abstractEntity;
    private EntityModel extendedEntity;
    private Boolean typeEntity;
    private Boolean securityEnabled;
    private List<RoleModel> contextRoles;
    private List<IndexModel> indexes;
    private String databaseRefName;

    private ReferenceObjectModel referenceObject;
    private Boolean reverseEngineer;

    public EntityModel() {
    }

    public EntityModel(Long entityId) {
        super(entityId);
    }

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

    /**
     * @return
     */
    @Column(name = "abstract_entity")
    public Boolean getAbstractEntity() {
        return abstractEntity;
    }

    /**
     * @param abstractEntity
     */
    public void setAbstractEntity(Boolean abstractEntity) {
        this.abstractEntity = abstractEntity;
    }

    /**
     * @return
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_extended_entity")
    public EntityModel getExtendedEntity() {
        return extendedEntity;
    }

    /**
     * @param extendedEntity
     */
    public void setExtendedEntity(EntityModel extendedEntity) {
        this.extendedEntity = extendedEntity;
    }

    /**
     * @return
     */
    @Column(name = "type_entity")
    public Boolean getTypeEntity() {
        return typeEntity;
    }

    /**
     * @param typeEntity
     */
    public void setTypeEntity(Boolean typeEntity) {
        this.typeEntity = typeEntity;
    }

    @Column(name = "security_enabled")
    public Boolean getSecurityEnabled() {
        return securityEnabled;
    }

    public void setSecurityEnabled(Boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    /**
     * @return Context Roles allowable for the entity.
     */
    @ManyToMany(targetEntity = RoleModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_entity_context_role",
            joinColumns = @JoinColumn(name = "id_entity", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_role", referencedColumnName = "id")
    )
    @ForeignKey(name = "FK_ENTITY_ROLES")
    public List<RoleModel> getContextRoles() {
        return contextRoles;
    }

    public void setContextRoles(List<RoleModel> contextRoles) {
        this.contextRoles = contextRoles;
    }

    /**
     * @return
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @ForeignKey(name = "fk_registry_node_index")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public List<IndexModel> getIndexes() {
        return indexes;
    }

    /**
     * @param indexes
     */
    public void setIndexes(List<IndexModel> indexes) {
        this.indexes = indexes;
    }

    @Column(name = "database_ref_name", length = 1024)
    public String getDatabaseRefName() {
        return databaseRefName;
    }

    public void setDatabaseRefName(String databaseRefName) {
        this.databaseRefName = databaseRefName;
    }

    /**
     * @return
     */
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reference_object", updatable = false)
    @ForeignKey(name = "fk_entity_reference_object")
    public ReferenceObjectModel getReferenceObject() {
        return referenceObject;
    }

    public void setReferenceObject(ReferenceObjectModel referenceObject) {
        this.referenceObject = referenceObject;
    }

    public Boolean getReverseEngineer() {
        return reverseEngineer;
    }

    public void setReverseEngineer(Boolean reverseEngineer) {
        this.reverseEngineer = reverseEngineer;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.ENTITY;
    }

}
