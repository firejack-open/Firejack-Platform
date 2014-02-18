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

import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.store.IStore;

import java.util.List;


public interface IResourceVersionStore<RV extends AbstractResourceVersionModel> extends IStore<RV, Long> {

    /**
     * @param id
     * @param culture
     * @param version
     * @return
     */
    RV findByResourceIdCultureAndVersion(Long id, Cultures culture, Integer version);

    /**
     * @param id
     * @param culture
     * @return
     */
    RV findLastVersionByResourceIdCulture(Long id, Cultures culture);

    /**
     * @param id
     * @return
     */
    RV findLastVersionByResourceId(Long id);

    /**
     * @param resourceLookup
     * @return
     */
    RV findLastVersionByLookup(String resourceLookup);

    /**
     * @param resourceId
     * @return
     */
    List<RV> findLastVersionsByResourceId(Long resourceId);

    /**
     * @param resourceId
     * @param version
     * @return
     */
    List<RV> findByResourceIdAndVersion(Long resourceId, Integer version);

    /**
     * @param resourceIds
     * @return
     */
    List<Cultures> findCulturesForLastVersionByResourceIds(final List<Long> resourceIds);

    /**
     * @param resourceId
     * @param version
     * @param culture
     * @return
     */
    RV createNewResourceVersion(long resourceId, int version, Cultures culture);

    /**
     * @param abstractResource
     * @param resourceVersions
     * @return
     */
    List<RV> createNewResourceVersions(AbstractResourceModel abstractResource, List<RV> resourceVersions);

    /**
     * @param resourceId
     * @return
     */
    List<RV> findAllResourceVersions(Long resourceId);

    /**
     * @param resourceId
     */
    void deleteAllByResourceId(Long resourceId);

    Integer deleteResourceVersion(RV entity);

	List<RV> readResourcesByLookupList(List<String> lookup);
}
