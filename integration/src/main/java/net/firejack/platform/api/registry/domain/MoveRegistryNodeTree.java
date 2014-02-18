package net.firejack.platform.api.registry.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

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
