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
import net.firejack.platform.core.domain.IdLookup;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlRootElement;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MoveSecuredRecordInfo extends AbstractDTO {
	private static final long serialVersionUID = 9220402144042442332L;

	private Long id;
    private String lookup;
    private IdLookup parent;
    private IdLookup[] oldParents;

    public MoveSecuredRecordInfo() {
    }

    public MoveSecuredRecordInfo(Long id, String lookup,
                                 IdLookup parent, IdLookup[] oldParents) {
        this.id = id;
        this.lookup = lookup;
        this.parent = parent;
        this.oldParents = oldParents;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public IdLookup getParent() {
        return parent;
    }

    public void setParent(IdLookup parent) {
        this.parent = parent;
    }

    public IdLookup[] getOldParents() {
        return oldParents;
    }

    public void setOldParents(IdLookup[] oldParents) {
        this.oldParents = oldParents;
    }

}