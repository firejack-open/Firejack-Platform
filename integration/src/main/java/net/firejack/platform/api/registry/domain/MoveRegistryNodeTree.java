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

package net.firejack.platform.api.registry.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class MoveRegistryNodeTree extends AbstractDTO {
	private static final long serialVersionUID = 818326466735951452L;

	private Long registryNodeId;
	private Long newRegistryNodeParentId;
	private Long oldRegistryNodeParentId;
	private Integer position;

	public Long getRegistryNodeId() {
		return registryNodeId;
	}

	public void setRegistryNodeId(Long registryNodeId) {
		this.registryNodeId = registryNodeId;
	}

	public Long getNewRegistryNodeParentId() {
		return newRegistryNodeParentId;
	}

	public void setNewRegistryNodeParentId(Long newRegistryNodeParentId) {
		this.newRegistryNodeParentId = newRegistryNodeParentId;
	}

	public Long getOldRegistryNodeParentId() {
		return oldRegistryNodeParentId;
	}

	public void setOldRegistryNodeParentId(Long oldRegistryNodeParentId) {
		this.oldRegistryNodeParentId = oldRegistryNodeParentId;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
}
