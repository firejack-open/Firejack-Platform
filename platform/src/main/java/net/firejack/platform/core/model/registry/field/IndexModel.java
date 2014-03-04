/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
