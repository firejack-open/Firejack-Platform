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

package net.firejack.platform.api.securitymanager.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.utils.CollectionUtils;
import net.firejack.platform.core.utils.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;



@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SecuredRecordNodePath extends AbstractDTO {

    public static final String EMPTY_PATH = "['']";
    private static final String VAL_COLON = ":";
    private static final String VAL_QUOTE = "'";

    private List<Long> pathEntries;

    public List<Long> getPathEntries() {
        return pathEntries;
    }

    public void setPathEntries(List<Long> pathEntries) {
        this.pathEntries = pathEntries;
    }

    /**
     * @return
     */
    public String composePath() {
        return pathEntries == null ?
                VAL_QUOTE + StringUtils.join(this.pathEntries, VAL_COLON) + VAL_QUOTE :
                EMPTY_PATH;
    }

    /**
     * @param id
     * @return
     */
    public boolean contains(Long id) {
        boolean result = false;
        if (this.pathEntries != null) {
            for (Long secRecId : this.pathEntries) {
                if (secRecId.equals(id)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecuredRecordNodePath)) return false;

        SecuredRecordNodePath srPath = (SecuredRecordNodePath) o;

        return CollectionUtils.isEqualCollection(pathEntries, srPath.pathEntries);
    }

    @Override
    public int hashCode() {
        return pathEntries != null ? pathEntries.hashCode() : 0;
    }

    public static SecuredRecordNodePath parse(String path) {
        if (StringUtils.isBlank(path) || path.length() < 2) {
            throw new IllegalArgumentException();
        }
        path = path.substring(1, path.length() - 1); //remove single quotes
        List<Long> pathEntries;
        if (StringUtils.isBlank(path)) {
            pathEntries = null;
        } else {
            String[] parentIds = path.split(VAL_COLON); //split by colon
            pathEntries = new ArrayList<Long>(parentIds.length);
            for (String parentId : parentIds) {
                pathEntries.add(Long.parseLong(parentId));
            }
        }
        SecuredRecordNodePath nodePath = new SecuredRecordNodePath();
        nodePath.setPathEntries(pathEntries);
        return nodePath;
    }
}