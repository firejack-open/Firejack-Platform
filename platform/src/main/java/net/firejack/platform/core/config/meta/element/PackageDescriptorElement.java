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

package net.firejack.platform.core.config.meta.element;

import net.firejack.platform.core.config.meta.IBasicRegistryNodeElement;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.INameProvider;
import net.firejack.platform.core.model.registry.IUIDProvider;

import javax.xml.bind.annotation.XmlAttribute;


public abstract class PackageDescriptorElement<Ent extends BaseEntityModel>
        implements IBasicRegistryNodeElement, INameProvider, IUIDProvider {

    private String name;
    private String description;
    private String path;
    private String uid;

    @Override
    @XmlAttribute
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @XmlAttribute
    public String getPath() {
        return path;
    }

    /**
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    @XmlAttribute
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return
     */
    public abstract Class<Ent> getEntityClass();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackageDescriptorElement)) return false;

        PackageDescriptorElement that = (PackageDescriptorElement) o;

        return (name == null ? that.name == null : name.equals(that.name)) &&
                !(description != null ?
                        !description.equals(that.description) : that.description != null) &&
                !(path != null ? !path.equals(that.path) : that.path != null) &&
                !(uid != null ? !uid.equals(that.uid) : that.uid != null);

    }

    @Override
    public int hashCode() {
        int result = name == null ? 0 : name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }
}