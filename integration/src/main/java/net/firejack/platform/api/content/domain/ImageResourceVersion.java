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

package net.firejack.platform.api.content.domain;

import net.firejack.platform.core.annotation.Property;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlRootElement;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ImageResourceVersion extends AbstractResourceVersion {
    private static final long serialVersionUID = 382857741440959127L;

    @Property
    private String title;
    @Property
    private Integer width;
    @Property
    private Integer height;
    @Property(name = "temporaryFilename")
    private String resourceFileTemporaryName;
    @Property(name = "originalFilename")
    private String resourceFileOriginalName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getStoredFilename() {
        String storedFileName = null;
        if (getId() != null && getVersion() != null && getCulture() != null) {
            storedFileName = getId() + "_" + getVersion() + "_" + getCulture().name();
        }
        return storedFileName;
    }

    public String getResourceFileTemporaryName() {
        return resourceFileTemporaryName;
    }

    public void setResourceFileTemporaryName(String resourceFileTemporaryName) {
        this.resourceFileTemporaryName = resourceFileTemporaryName;
    }

    public String getResourceFileOriginalName() {
        return resourceFileOriginalName;
    }

    public void setResourceFileOriginalName(String resourceFileOriginalName) {
        this.resourceFileOriginalName = resourceFileOriginalName;
    }

}
