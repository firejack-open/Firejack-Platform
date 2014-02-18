/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.service.registry.broker.entity;

import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IDomainStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class encapsulates the functionality of searching for the actors
 */
@TrackDetails
@Component("searchEntityBroker")
public class SearchEntityBroker extends ListBroker<EntityModel, Entity, NamedValues> {

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IDomainStore domainStore;

    private List<String> domainLookupPrefixes;

    @Override
    protected Integer getTotal(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
        String terms = (String) request.getData().get("terms");
        Long domainId = (Long) request.getData().get("domainId");
        String packageLookup = (String) request.getData().get("packageLookup");

        domainLookupPrefixes = getDomainLookupPrefixesByDataSource(domainId, packageLookup);

        Integer count = 0;
        if (!domainLookupPrefixes.isEmpty()) {
            count = entityStore.searchCountByDomain(terms, domainLookupPrefixes);
        }
        return count;
    }

    @Override
    protected List<EntityModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
        String terms = (String) request.getData().get("terms");

        Integer offset = (Integer) request.getData().get("offset");
        Integer limit = (Integer) request.getData().get("limit");
        String sortColumn = (String) request.getData().get("sortColumn");
        String sortDirection = (String) request.getData().get("sortDirection");

        List<EntityModel> entityModels = new ArrayList<EntityModel>();
        if (!domainLookupPrefixes.isEmpty()) {
            entityModels = entityStore.searchByDomain(terms, domainLookupPrefixes, new Paging(offset, limit, sortColumn, sortDirection));
        }
        return entityModels;
    }

    private List<String> getDomainLookupPrefixesByDataSource(Long domainId, String packageLookup) {
        List<String> domainLookupPrefixes = new ArrayList<String>();
        Map<String, DatabaseModel> dataSourcesByPackageLookup = domainStore.findAllWithDataSourcesByPackageLookup(packageLookup);
        if (domainId != null) {
            DomainModel domainModel = domainStore.findById(domainId);
            DatabaseModel databaseModel = dataSourcesByPackageLookup.get(domainModel.getLookup());
            if (databaseModel == null) {
                List<DomainModel> domainModels = domainStore.findAllByLikeLookupPrefix(packageLookup + ".%");
                for (DomainModel domain : domainModels) {
                    databaseModel = dataSourcesByPackageLookup.get(domain.getLookup());
                    if (databaseModel == null) {
                        domainLookupPrefixes.add(domain.getLookup() + ".%");
                    }
                }
            } else {
                domainLookupPrefixes.add(domainModel.getLookup() + ".%");
            }
        } else {
            List<DomainModel> domainModels = domainStore.findAllByLikeLookupPrefix(packageLookup + "%");
            for (DomainModel domain : domainModels) {
                DatabaseModel databaseModel = dataSourcesByPackageLookup.get(domain.getLookup());
                if (databaseModel == null) {
                    domainLookupPrefixes.add(domain.getLookup() + ".%");
                }
            }
        }
        return domainLookupPrefixes;
    }

}