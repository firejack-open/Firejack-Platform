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
