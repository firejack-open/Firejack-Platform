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

package net.firejack.platform.core.model.registry.resource;


import net.firejack.platform.api.content.model.ResourceType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;


@Entity
public class ResourceModel<RV extends AbstractResourceVersionModel> extends AbstractResourceModel<RV> {

	private static final long serialVersionUID = 5574677521298661753L;
	protected RV resourceVersion;
	private List<CollectionMemberModel> members;

    @Override
    @Transient
    public RV getResourceVersion() {
        return resourceVersion;
    }

    @Override
    public void setResourceVersion(RV resourceVersion) {
        this.resourceVersion = resourceVersion;
    }


	@OneToMany(mappedBy = "reference", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<CollectionMemberModel> getMembers() {
		return members;
	}

	private void setMembers(List<CollectionMemberModel> members) {
		this.members = members;
	}

    @Override
    @Transient
    public ResourceType getResourceType() {
        return null;
    }

    @Override
    @Transient
    public boolean isDisplayedInTree() {
        return false;
    }

}
