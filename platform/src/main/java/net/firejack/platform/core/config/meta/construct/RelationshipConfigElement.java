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

import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.IFieldElementContainer;
import net.firejack.platform.core.config.meta.IRelationshipElement;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;


class RelationshipConfigElement extends BaseConfigElement implements IRelationshipElement, IFieldElementContainer {

    private String hint;
    private RelationshipType type;
    private Reference source;
    private Reference target;
    private List<IFieldElement> fields;
    private RelationshipOption onDeleteOptions;
    private RelationshipOption onUpdateOptions;
    private Boolean required;
    private boolean sortable;
    private Boolean reverseEngineer;

    RelationshipConfigElement(String name) {
        super(name);
    }

    @Override
    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public RelationshipType getType() {
        return type;
    }

    public void setType(RelationshipType type) {
        this.type = type;
    }

    @Override
    public Reference getSource() {
        return source;
    }

    public void setSource(Reference source) {
        this.source = source;
    }

    @Override
    public RelationshipOption getOnDeleteOptions() {
        return onDeleteOptions;
    }

    public void setOnDeleteOptions(RelationshipOption onDeleteOptions) {
        this.onDeleteOptions = onDeleteOptions;
    }

    @Override
    public RelationshipOption getOnUpdateOptions() {
        return onUpdateOptions;
    }

    public void setOnUpdateOptions(RelationshipOption onUpdateOptions) {
        this.onUpdateOptions = onUpdateOptions;
    }

    @Override
    public Reference getTarget() {
        return target;
    }

    public void setTarget(Reference target) {
        this.target = target;
    }

    @Override
    public IFieldElement[] getFields() {
        return fields == null ? null : fields.toArray(new IFieldElement[fields.size()]);
    }

    public void setFields(List<IFieldElement> fields) {
        this.fields = fields;
    }

    @Override
    public boolean isRequired() {
        return required != null && required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public boolean isSortable() {
        return sortable;
    }

    void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public Boolean getReverseEngineer() {
        return reverseEngineer;
    }

    public void setReverseEngineer(Boolean reverseEngineer) {
        this.reverseEngineer = reverseEngineer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationshipConfigElement)) return false;

        RelationshipConfigElement that = (RelationshipConfigElement) o;

        boolean propEmpty = fields == null || fields.isEmpty();
        boolean thatPropEmpty = that.fields == null || that.fields.isEmpty();
        if ((propEmpty && !thatPropEmpty) || (!propEmpty && thatPropEmpty)) {
            return false;
        } else if (!propEmpty && !thatPropEmpty && !CollectionUtils.isEqualCollection(fields, that.fields)) {
            return false;
        }

        return name.equals(that.name) && onDeleteOptions == that.onDeleteOptions &&
                onUpdateOptions == that.onUpdateOptions &&
                !((required != null && required) ^ (that.required != null && that.required)) &&
                source.equals(that.source) && type == that.type &&
                ((type != RelationshipType.TREE && target.equals(that.target)) || type == RelationshipType.TREE);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + source.hashCode();
        result = 31 * result + (target == null ? 0 : target.hashCode());
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        result = 31 * result + (onDeleteOptions != null ? onDeleteOptions.hashCode() : 0);
        result = 31 * result + (onUpdateOptions != null ? onUpdateOptions.hashCode() : 0);
        result = 31 * result + (required != null ? required.hashCode() : 0);
        return result;
    }
}