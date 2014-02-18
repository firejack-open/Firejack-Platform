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

package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.model.registry.field.FieldModel;

import javax.persistence.*;

@Entity
@Table(name = "opf_activity_field")
public class ActivityFieldModel extends BaseEntityModel {
    private static final long serialVersionUID = 3496759521164420213L;

    private ActivityModel activity;
    private FieldModel field;
    private RelationshipModel relationship;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_activity")
    public ActivityModel getActivity() {
        return activity;
    }

    public void setActivity(ActivityModel activity) {
        this.activity = activity;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_field")
    public FieldModel getField() {
        return field;
    }

    public void setField(FieldModel field) {
        this.field = field;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_relationship")
    public RelationshipModel getRelationship() {
        return relationship;
    }

    public void setRelationship(RelationshipModel relationship) {
        this.relationship = relationship;
    }

}
