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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.RESTMethod;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.report.ReportFieldModel;
import net.firejack.platform.core.model.registry.report.ReportModel;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Class provides access to the data for the registry nodes of the database type
 */
@Component("reportStore")
public class ReportStore extends RegistryNodeStore<ReportModel> implements IReportStore {

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IActionStore actionStore;

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(ReportModel.class);
    }

    @Override
    @Transactional
    public void saveForGenerator(ReportModel report) {
        save(report);
        actionStore.createWithPermissionByEntity(report, RESTMethod.ADVANCED_SEARCH);
    }

    @Override
    @Transactional
    public void save(ReportModel report) {
        List<ReportFieldModel> fields = report.getFields();
        for (ReportFieldModel field : fields) {
            field.setReport(report);
        }

        EntityModel entityModel = entityStore.findById(report.getParent().getId());

        report.setServerName(entityModel.getServerName());
        report.setPort(entityModel.getPort());
        report.setParentPath(entityModel.getParentPath());
        report.setUrlPath(entityModel.getUrlPath() + "/" + StringUtils.normalize(report.getName()));
        report.setProtocol(entityModel.getProtocol());
        report.setStatus(entityModel.getStatus());

        super.save(report);
    }

    @Transactional(readOnly = true)
    public List<ReportModel> findAllByLikeLookupPrefix(final String lookupPrefix) {
		List<ReportModel> models = super.findAllByLikeLookupPrefix(lookupPrefix);
		for (ReportModel model : models) {
            List<ReportFieldModel> fields = model.getFields();
            Hibernate.initialize(fields);
            if (fields != null) {
                for (ReportFieldModel field : fields) {
                    Hibernate.initialize(field.getRelationships());
                }
            }
        }
		return models;
	}

    @Override
    public void deleteAllByRegistryNodeId(Long id) {
        List<ReportModel> models = findAllByParentIdWithFilter(id, null);
        super.deleteAll(models);

    }
}
