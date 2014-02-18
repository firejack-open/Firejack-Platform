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

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.element.authority.EntityRole;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.utils.ArrayUtils;

import java.util.Arrays;
import java.util.List;


public class EntityConfigElement extends BaseConfigElement implements IEntityElement, IFieldElementContainer, IEntityElementContainer {

    private List<IFieldElement> fields;
    private List<IEntityElement> configuredEntities;
    private List<IIndexElement> indexes;
    private List<CompoundKeyColumnsRule> compoundKeyColumnsRules;
    private String extendedEntityPath;
    private Boolean abstractEntity;
    private Boolean typeEntity;
    private Boolean securityEnabled;
    private boolean subEntity;
    private String alias;
    private INamedPackageDescriptorElement parent;
    private ReferenceObjectData referenceObject;
    private EntityRole[] allowableContextRoles;
    private String databaseRefName;
    private Boolean reverseEngineer;

    EntityConfigElement(String name) {
        super(name);
    }

    @Override
    public IFieldElement[] getFields() {
        return fields == null ? null : fields.toArray(new IFieldElement[fields.size()]);
    }

    @Override
    public void setFields(List<IFieldElement> fields) {
        this.fields = fields;
    }

    @Override
    public IEntityElement[] getConfiguredEntities() {
        return configuredEntities == null ? null :
                configuredEntities.toArray(new IEntityElement[configuredEntities.size()]);
    }

    public void setConfiguredEntities(List<IEntityElement> configuredEntities) {
        this.configuredEntities = configuredEntities;
    }


    public IIndexElement[] getIndexes() {
        return indexes == null ? null : indexes.toArray(new IIndexElement[indexes.size()]);
    }

    public void setIndexes(List<IIndexElement> indexes) {
        this.indexes = indexes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompoundKeyColumnsRule[] getCompoundKeyColumnsRules() {
        return DiffUtils.getArray(compoundKeyColumnsRules, CompoundKeyColumnsRule.class);
    }

    public void setCompoundKeyColumnsRules(List<CompoundKeyColumnsRule> compoundKeyColumns) {
        this.compoundKeyColumnsRules = compoundKeyColumns;
    }

    @Override
    public String getExtendedEntityPath() {
        return extendedEntityPath;
    }

    public void setExtendedEntityPath(String extendedEntityPath) {
        this.extendedEntityPath = extendedEntityPath;
    }

    @Override
    public boolean isAbstractEntity() {
        return abstractEntity != null && abstractEntity;
    }

    public void setAbstractEntity(Boolean abstractEntity) {
        this.abstractEntity = abstractEntity;
    }

    @Override
    public boolean isTypeEntity() {
        return typeEntity != null && typeEntity;
    }

    public void setTypeEntity(boolean typeEntity) {
        this.typeEntity = typeEntity;
    }

    @Override
    public Boolean isSecurityEnabled() {
        return securityEnabled != null && securityEnabled;
    }

    @Override
    public void setSecurityEnabled(Boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    public boolean isSubEntity() {
        return subEntity;
    }

    public void setSubEntity(boolean subEntity) {
        this.subEntity = subEntity;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public INamedPackageDescriptorElement getParent() {
        return parent;
    }

    public void setParent(INamedPackageDescriptorElement parent) {
        this.parent = parent;
    }

    @Override
    public EntityRole[] getAllowableContextRoles() {
        return allowableContextRoles;
    }

    @Override
    public void setAllowableContextRoles(EntityRole[] allowableContextRoles) {
        this.allowableContextRoles = allowableContextRoles;
    }

    public String getDatabaseRefName() {
        return databaseRefName;
    }

    public void setDatabaseRefName(String databaseRefName) {
        this.databaseRefName = databaseRefName;
    }

    public Boolean getReverseEngineer() {
        return reverseEngineer;
    }

    public void setReverseEngineer(Boolean reverseEngineer) {
        this.reverseEngineer = reverseEngineer;
    }

    public ReferenceObjectData getReferenceObject() {
        return referenceObject;
    }

    public void setReferenceObject(ReferenceObjectData referenceObject) {
        this.referenceObject = referenceObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityConfigElement)) return false;

        EntityConfigElement that = (EntityConfigElement) o;

        boolean propEmpty = fields == null || fields.isEmpty();
        boolean thatPropEmpty = that.fields == null || that.fields.isEmpty();
        if (((propEmpty && !thatPropEmpty) || (!propEmpty && thatPropEmpty)) ||
                (!propEmpty && !thatPropEmpty && !fields.equals(that.fields))) {
            return false;
        }

        if (!name.equals(that.name)) return false;

        propEmpty = typeEntity != null && typeEntity;
        thatPropEmpty = that.typeEntity != null && that.typeEntity;
        if (propEmpty != thatPropEmpty) {
            return false;
        }

        propEmpty = securityEnabled != null && securityEnabled;
        thatPropEmpty = that.securityEnabled != null && that.securityEnabled;
        if (propEmpty != thatPropEmpty) {
            return false;
        }

        propEmpty = allowableContextRoles == null || allowableContextRoles.length == 0;
        thatPropEmpty = that.allowableContextRoles == null || that.allowableContextRoles.length == 0;

        if (propEmpty != thatPropEmpty || (!propEmpty && (
                !ArrayUtils.containsAll(allowableContextRoles, that.allowableContextRoles) ||
                        !ArrayUtils.containsAll(that.allowableContextRoles, allowableContextRoles)))) {
            return false;
        }

        propEmpty = referenceObject == null;
        thatPropEmpty = that.referenceObject == null;

        return propEmpty == thatPropEmpty && (propEmpty || referenceObject.equals(that.referenceObject));
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        result = 31 * result + (configuredEntities != null ? configuredEntities.hashCode() : 0);
        result = 31 * result + (compoundKeyColumnsRules != null ? compoundKeyColumnsRules.hashCode() : 0);
        result = 31 * result + (allowableContextRoles != null ? Arrays.hashCode(allowableContextRoles) : 0);
        return result;
    }
}