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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.RESTMethod;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.IEntity;
import net.firejack.platform.core.utils.documentation.DocumentationEntryType;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface IActionStore extends IRegistryNodeStore<ActionModel>, IResourceAccessFieldsStore<ActionModel> {

    /**
     * @param filter
     * @return
     */
    List<ActionModel> findAllWithFilterWithoutFields(SpecifiedIdsFilter<Long> filter);

    /**
     * @return
     */
    List<ActionModel> findAllWithPermissions();

    /**
     * @return
     */
    Map<String, List<ActionModel>> findAllWithPermissionsByPackage();

    /**
     * @param baseLookup
     * @return
     */
    List<ActionModel> findAllWithPermissions(String baseLookup);

    /**
     * @param lookupPrefix
     * @param name
     * @return
     */
    List<ActionModel> findAllByLikeLookupAndName(String lookupPrefix, String name);

    /**
     * @param urlPath
     * @param protocol
     * @return
     */
    ActionModel findByUrlPath(String urlPath, EntityProtocol protocol);

    /**
     * @param action
     * @param examplesToCreate
     */
    void saveForGenerator(ActionModel action, Set<DocumentationEntryType> examplesToCreate);

    /**
     * @param action
     * @param requiredToRegenerateExamples
     */
    void saveWithGeneratingExamples(ActionModel action, boolean requiredToRegenerateExamples);

    /**
     * @param entity
     * @param restMethod
     * @return
     */
    ActionModel createWithPermissionByEntity(IEntity entity, RESTMethod restMethod);

	void updateActionPath(ActionModel action);

}