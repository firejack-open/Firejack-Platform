/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.api.authority.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The {@code UserPermission} object is a simple, serializable data object that allows
 * for specification of either a simple permission or a context-aware permission check. It is
 * used for performing permission checks at run-time against the {@code BusinessContext}.
 * <p/>
 * The Open Flame Framework will automatically check for known and registered permissions as
 * dictated by service meta-data configurations. Globally granted permissions always grant
 * context-sensitive permissions by default, which means a context-sensitive permission check also
 * implicitly checks the global permission before it checks the actual context-aware permission.
 */
@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class UserPermission extends AbstractDTO {
    private static final long serialVersionUID = -3388710734969345983L;

    /**
     * the fully qualified name of the permission in Open Flame
     */
    private String permission;
    /**
     * the fully qualified entity type lookup for the permission
     */
    private String entityType;
    /**
     * the identifier for the specific entity (optional)
     */
    private String entityId;

    /**
     * Default constructor
     */
    public UserPermission() {
    }

    /**
     * The default constructor takes in only the name of a permission, which should be the full
     * lookup name from the Open Flame Platform console.
     *
     * @param permission String the fully qualified lookup of the permission
     */
    public UserPermission(String permission) {
        this.permission = permission;
    }

    /**
     * This constructor allows for construction of a permission that includes context-sensitive
     * data. In this case, the permission may be checked within the context of a specific object
     * of known type and specific ID. If required, the framework can translate the entityType and
     * entityId parameters into a know secured record and trace that through to it's parents, but
     * this is done behind the scenes and only if there is a corresponding secured record for the
     * object in question.
     *
     * @param permission String the fully qualified lookup name for the permission
     * @param entityType String the fully qualified lookup name for the entity type
     * @param entityId   String the unique id for the entity (which can be a number as a string)
     */
    public UserPermission(String permission, String entityType, String entityId) {
        this.permission = permission;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

        @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("UserPermission");
        sb.append("{permission='").append(permission).append('\'');
        sb.append(", entityType='").append(entityType).append('\'');
        sb.append(", entityId='").append(entityId).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPermission that = (UserPermission) o;

        if (entityId != null ? !entityId.equals(that.entityId) : that.entityId != null) return false;
        if (entityType != null ? !entityType.equals(that.entityType) : that.entityType != null) return false;
        if (permission != null ? !permission.equals(that.permission) : that.permission != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = permission != null ? permission.hashCode() : 0;
        result = 31 * result + (entityType != null ? entityType.hashCode() : 0);
        result = 31 * result + (entityId != null ? entityId.hashCode() : 0);
        return result;
    }

}