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

package net.firejack.platform.core.config.meta.factory;


import net.firejack.platform.core.config.meta.element.process.StatusElement;
import net.firejack.platform.core.model.registry.process.StatusModel;
import net.firejack.platform.core.store.registry.IProcessStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class StatusElementFactory extends
        PackageDescriptorConfigElementFactory<StatusModel, StatusElement> {

    @Autowired
    @Qualifier("processStore")
    protected IProcessStore processStore;

    /***/
    public StatusElementFactory() {
        setElementClass(StatusElement.class);
        setEntityClass(StatusModel.class);
    }

    @Override
    protected void initEntitySpecific(StatusModel status, StatusElement statusElement) {
        super.initEntitySpecific(status, statusElement);
        status.setSortPosition(statusElement.getOrder());
        /*status.setName(statusElement.getName());
        status.setDescription(statusElement.getDescription());
        if (StringUtils.isNotBlank(statusElement.getPath())) {
            status.setPath(DiffUtils.lookupByRefPath(statusElement.getPath()));
            ProcessModel parent = processStore.findByLookup(status.getPath());
            status.setParent(parent);
        }
        status.setLookup(DiffUtils.lookup(status.getPath(), status.getName()));
        status.set*/
    }

    @Override
    protected void initDescriptorElementSpecific(StatusElement statusElement, StatusModel status) {
        super.initDescriptorElementSpecific(statusElement, status);
        statusElement.setOrder(status.getSortPosition());
        /*statusElement.setDescription(status.getDescription());
        statusElement.setName(status.getName());*/
    }
}