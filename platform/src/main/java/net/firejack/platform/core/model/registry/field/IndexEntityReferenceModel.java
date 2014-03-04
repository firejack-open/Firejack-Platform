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

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

@Entity
@Table(name = "opf_index_entity_reference",
       uniqueConstraints = {
               @UniqueConstraint(name = "UK_INDEX_REFERENCE_NAME",
                                 columnNames = {"id_entity", "id_index", "column_name"})
       }
)
public class IndexEntityReferenceModel extends BaseEntityModel {
    private static final long serialVersionUID = 9131376981138123627L;

    private String columnName;
    private EntityModel entityModel;
    private IndexModel index;

    @Column(name = "column_name")
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entity")
	@ForeignKey(name = "fk_entity_reference_entity")
    public EntityModel getEntityModel() {
        return entityModel;
    }

    public void setEntityModel(EntityModel entityModel) {
        this.entityModel = entityModel;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_index")
	@ForeignKey(name = "fk_entity_reference_index")
    public IndexModel getIndex() {
        return index;
    }

    public void setIndex(IndexModel index) {
        this.index = index;
    }
}
