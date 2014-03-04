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

package net.firejack.platform.web.controller.wrapper;

import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;

import java.util.List;

public class ReferenceWrapper {

    private RegistryNodeModel reference;
    private RegistryNodeType type;
    private List<ReferenceWrapper> childrenReferences;

    public ReferenceWrapper(RegistryNodeModel reference) {
        this.reference = reference;
        this.type = reference.getType();
    }

    public RegistryNodeModel getReference() {
        return reference;
    }

    public void setReference(RegistryNodeModel reference) {
        this.reference = reference;
    }

    public RegistryNodeType getType() {
        return type;
    }

    public void setType(RegistryNodeType type) {
        this.type = type;
    }

    public List<ReferenceWrapper> getChildrenReferences() {
        return childrenReferences;
    }

    public void setChildrenReferences(List<ReferenceWrapper> childrenReferences) {
        this.childrenReferences = childrenReferences;
    }
}
