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

package net.firejack.platform.core.store.registry.resource;

import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.model.registry.resource.HtmlResourceModel;

public interface IHtmlResourceStore extends IResourceStore<HtmlResourceModel> {

	void createDescription(ActionModel action, CollectionModel collection);
    /**
     * @param registryNode
     */
    void createAutoDescription(RegistryNodeModel registryNode);

	/**
	 * @param action
	 * @param collection
	 */
	void createRestExample(ActionModel action, CollectionModel collection);

	/**
	 * @param action
	 * @param collection
	 */
	void createSoapExample(ActionModel action, CollectionModel collection);

}
