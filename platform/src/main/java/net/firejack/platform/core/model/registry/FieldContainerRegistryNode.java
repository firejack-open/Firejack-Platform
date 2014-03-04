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
