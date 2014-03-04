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