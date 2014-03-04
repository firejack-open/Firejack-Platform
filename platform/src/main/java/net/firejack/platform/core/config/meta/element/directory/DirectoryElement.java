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

package net.firejack.platform.core.config.meta.element.directory;

import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.IFieldElementContainer;
import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;

import java.util.List;


public class DirectoryElement extends PackageDescriptorElement<DirectoryModel> implements IFieldElementContainer {

    private DirectoryType directoryType;
    private String serverName;
    private String urlPath;
    private RegistryNodeStatus status;
    private List<IFieldElement> fields;

    /**
     * @return
     */
    public DirectoryType getDirectoryType() {
        return directoryType;
    }

    /**
     * @param directoryType
     */
    public void setDirectoryType(DirectoryType directoryType) {
        this.directoryType = directoryType;
    }

    /**
     * @return
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return
     */
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * @param urlPath
     */
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    /**
     * @return
     */
    public RegistryNodeStatus getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(RegistryNodeStatus status) {
        this.status = status;
    }

    @Override
    public IFieldElement[] getFields() {
        return fields == null ? null : fields.toArray(new IFieldElement[fields.size()]);
    }

    @Override
    public void setFields(List<IFieldElement> fields) {
        this.fields = fields;
    }

    @Override
    public Class<DirectoryModel> getEntityClass() {
        return DirectoryModel.class;
    }
}
