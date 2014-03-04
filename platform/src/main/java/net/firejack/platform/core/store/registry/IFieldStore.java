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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.store.lookup.ILookupStore;

import java.util.List;


public interface IFieldStore extends ILookupStore<FieldModel, Long> {

    /**
     * @param registryNodeId
     * @return
     */
    List<FieldModel> findFieldsByRegistryNodeId(Long registryNodeId);

    /**
     * @param parentLookup
     * @param fieldName
     * @return
     */
    FieldModel findByParentLookupAndName(String parentLookup, String fieldName);

    /**
     * @param parentLookup
     * @param fieldName
     */
    void deleteByParentLookupAndName(String parentLookup, String fieldName);

    /**
     * @param registryNodeId
     */
    void deleteAllByRegistryNodeId(Long registryNodeId);

}
