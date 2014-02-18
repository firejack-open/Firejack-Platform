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

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.api.registry.model.IndexType;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.IIndexElement;

import java.util.List;

public class IndexConfigElement extends BaseConfigElement implements IIndexElement {

    private IndexType type;
    private List<Reference> entities;
    private List<IFieldElement> fields;
    private Reference relationship;

    public IndexConfigElement(String name) {
        super(name);
    }

    public IndexType getType() {
        return type;
    }

    public void setType(IndexType type) {
        this.type = type;
    }

    public List<Reference> getEntities() {
        return entities;
    }

    public void setEntities(List<Reference> entities) {
        this.entities = entities;
    }

    public List<IFieldElement> getFields() {
        return fields;
    }

    public void setFields(List<IFieldElement> fields) {
        this.fields = fields;
    }

    public Reference getRelationship() {
        return relationship;
    }

    public void setRelationship(Reference relationship) {
        this.relationship = relationship;
    }
}
