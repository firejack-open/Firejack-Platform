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

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;

import java.util.List;


public interface IResourceStore<R extends AbstractResourceModel> extends IRegistryNodeStore<R> {

    /**
     * @param collectionId
     * @return
     */
    List<Cultures> findAvailableCulturesByCollectionId(Long collectionId);

    /**
     * @param registryNodeIds
     * @param term
     * @param filter
     * @return
     */
    List<R> findAllBySearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter);

    /**
     * @param term
     * @param filter
     * @return
     */
    List<R> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter);

    List<R> findAllByLikeLookupPrefix(String lookup);

    void save(R resource);

    List<AbstractResourceVersionModel<R>> save(R resource, List<AbstractResourceVersionModel<R>> resourceVersions);

    void delete(R registryNode);

    void mergeForGenerator(R resource, List<AbstractResourceVersionModel<R>> resourceVersionList);

    Integer setMaxResourceVersion(R resource);

}
