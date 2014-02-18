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

package net.firejack.platform.core.config.meta.element;

import net.firejack.platform.core.config.meta.IBasicRegistryNodeElement;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.INameProvider;
import net.firejack.platform.core.model.registry.IUIDProvider;

import javax.xml.bind.annotation.XmlAttribute;


public abstract class PackageDescriptorElement<Ent extends BaseEntityModel>
        implements IBasicRegistryNodeElement, INameProvider, IUIDProvider {

    private String name;
    private String description;
    private String path;
    private String uid;

    @Override
    @XmlAttribute
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @XmlAttribute
    public String getPath() {
        return path;
    }

    /**
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    @XmlAttribute
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return
     */
    public abstract Class<Ent> getEntityClass();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackageDescriptorElement)) return false;

        PackageDescriptorElement that = (PackageDescriptorElement) o;

        return (name == null ? that.name == null : name.equals(that.name)) &&
                !(description != null ?
                        !description.equals(that.description) : that.description != null) &&
                !(path != null ? !path.equals(that.path) : that.path != null) &&
                !(uid != null ? !uid.equals(that.uid) : that.uid != null);

    }

    @Override
    public int hashCode() {
        int result = name == null ? 0 : name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }
}