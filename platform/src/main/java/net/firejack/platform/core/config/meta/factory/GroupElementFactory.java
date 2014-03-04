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

import net.firejack.platform.core.config.meta.element.directory.GroupElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import net.firejack.platform.core.store.registry.IDirectoryStore;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class GroupElementFactory extends PackageDescriptorConfigElementFactory<GroupModel, GroupElement> {

    @Autowired
    @Qualifier("directoryStore")
    private IDirectoryStore directoryStore;

    public GroupElementFactory() {
        setElementClass(GroupElement.class);
        setEntityClass(GroupModel.class);
    }

    @Override
    protected void initDescriptorElementSpecific(GroupElement groupElement, GroupModel group) {
        super.initDescriptorElementSpecific(groupElement, group);
        if (group.getDirectory() != null) {
            if (Hibernate.isInitialized(group.getDirectory())) {
                groupElement.setDirectoryRef(group.getDirectory().getLookup());
                groupElement.setPath(null);
            }
        } else if (group.getParent() != null && Hibernate.isInitialized(group.getParent()) &&
                group.getParent().getType() == RegistryNodeType.DIRECTORY) {
            groupElement.setDirectoryRef(group.getParent().getLookup());
        }
    }

    @Override
    protected void initEntitySpecific(GroupModel group, GroupElement groupElement) {
        super.initEntitySpecific(group, groupElement);

        if (StringUtils.isNotBlank(groupElement.getDirectoryRef())) {
            DirectoryModel directory = directoryStore.findByLookup(groupElement.getDirectoryRef());
            if (directory == null) {
                throw new BusinessFunctionException(
                        "No such directory found by lookup = '" + groupElement.getDirectoryRef() + "'");
            }
            group.setDirectory(directory);
            group.setPath(groupElement.getDirectoryRef());
            group.setLookup(DiffUtils.lookup(group.getPath(), group.getName()));
            group.setParent(directory);
        } else if (group.getParent() != null && group.getParent().getType() == RegistryNodeType.DIRECTORY) {
            group.setDirectory((DirectoryModel) group.getParent());
        }
    }

}