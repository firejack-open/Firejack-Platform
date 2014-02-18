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

package net.firejack.platform.core.model.registry.field;

import net.firejack.platform.api.registry.model.IndexType;
import net.firejack.platform.core.annotation.Lookup;
import net.firejack.platform.core.model.registry.IAllowCreateAutoDescription;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.utils.IHasName;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@Entity
@XmlTransient
@Lookup(parent = RegistryNodeModel.class, suffix = "#index")
@Table(name = "opf_entity_index")
public class IndexModel extends LookupModel<EntityModel> implements IHasName, IAllowCreateAutoDescription {
    private static final long serialVersionUID = -7732961317538380854L;

    private List<IndexEntityReferenceModel> references;
    private List<FieldModel> fields;
    private IndexType indexType;
    private RelationshipModel relationship;

    /**
     * @return
     */
    //@OneToMany(mappedBy = "index", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "index", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_ENTITY_INDEX_REFERENCES")
    public List<IndexEntityReferenceModel> getReferences() {
        return references;
    }

    public void setReferences(List<IndexEntityReferenceModel> references) {
        this.references = references;
    }

    /**
     * @return
     */
    @ManyToMany(targetEntity = FieldModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_entity_index_fields",
            joinColumns = @JoinColumn(name = "id_index", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_field", referencedColumnName = "id")
    )
    @ForeignKey(name = "FK_ENTITY_INDEX_FIELDS")
    public List<FieldModel> getFields() {
        return fields;
    }

    /**
     * @param fields
     */
    public void setFields(List<FieldModel> fields) {
        this.fields = fields;
    }

    @Enumerated(EnumType.STRING)
    public IndexType getIndexType() {
        return indexType;
    }

    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_relationship")
    @ForeignKey(name = "fk_entity_index_relationship")
    public RelationshipModel getRelationship() {
        return relationship;
    }

    public void setRelationship(RelationshipModel relationship) {
        this.relationship = relationship;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.INDEX;
    }

}
