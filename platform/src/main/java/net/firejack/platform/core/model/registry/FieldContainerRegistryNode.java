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

import net.firejack.platform.core.model.registry.field.FieldModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;


@Entity
public class FieldContainerRegistryNode extends RegistryNodeModel {

    private static final long serialVersionUID = -6995042644060369168L;
    private List<FieldModel> fields;

    public FieldContainerRegistryNode() {
    }

    public FieldContainerRegistryNode(Long id) {
        super(id);
    }

    /**
     * @return
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @ForeignKey(name = "fk_registry_node_field")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public List<FieldModel> getFields() {
        return fields;
    }

    /**
     * @param fields
     */
    public void setFields(List<FieldModel> fields) {
        this.fields = fields;
    }

}
