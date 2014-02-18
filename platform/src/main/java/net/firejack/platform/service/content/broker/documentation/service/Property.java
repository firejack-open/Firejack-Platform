/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.service.content.broker.documentation.service;

import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.utils.IHasName;


public class Property<E extends IHasName> {

	private E entity;
	private ResourceModel resource;
	private EntityModel data;

	/** @param entity  */
	public Property(E entity) {
		this.entity = entity;
	}

	/** @return  */
	public E getEntity() {
		return entity;
	}

	/** @param entity  */
	public void setEntity(E entity) {
		this.entity = entity;
	}

	/** @return  */
	public ResourceModel getResource() {
		return resource;
	}

	/** @param resource  */
	public void setResource(ResourceModel resource) {
		this.resource = resource;
	}

	public EntityModel getData() {
		return data;
	}

	public void setData(EntityModel data) {
		this.data = data;
	}
}
