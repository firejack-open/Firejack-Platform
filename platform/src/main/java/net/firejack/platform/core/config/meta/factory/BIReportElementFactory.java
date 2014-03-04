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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.construct.BIReportElement;
import net.firejack.platform.core.config.meta.construct.BIReportField;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.bi.BIReportFieldModel;
import net.firejack.platform.core.model.registry.bi.BIReportModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IFieldStore;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


public class BIReportElementFactory extends PackageDescriptorConfigElementFactory<BIReportModel, BIReportElement> {

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IFieldStore fieldStore;

    public BIReportElementFactory() {
        setElementClass(BIReportElement.class);
        setEntityClass(BIReportModel.class);
    }

    @Override
    protected void initDescriptorElementSpecific(BIReportElement reportElement, BIReportModel report) {
        super.initDescriptorElementSpecific(reportElement, report);
        List<BIReportFieldModel> fields = report.getFields();
        if (Hibernate.isInitialized(fields)) {
            List<BIReportField> reportFields = new ArrayList<BIReportField>(fields.size());
            for (BIReportFieldModel field : fields) {
                BIReportField element = new BIReportField();
                element.setEntity(field.getEntity().getLookup());
                FieldModel fieldModel = field.getField();
                if (fieldModel != null) {
                    element.setField(fieldModel.getLookup());
                }
                element.setDisplayName(field.getDisplayName());
                element.setCount(field.getCount());

                reportFields.add(element);
            }
            reportElement.setFields(reportFields);
        }
        reportElement.setName(report.getName());
        reportElement.setPath(report.getPath());
        reportElement.setDescription(report.getDescription());
    }

    @Override
    protected void initEntitySpecific(BIReportModel report, BIReportElement reportElement) {
        super.initEntitySpecific(report, reportElement);
        List<BIReportField> reportFields = reportElement.getFields();
        if (reportFields != null) {
            Set<String> entities = new HashSet<String>(reportFields.size());
            Set<String> fields = new HashSet<String>(reportFields.size());

            for (BIReportField reportField : reportFields) {
                String entity = reportField.getEntity();
                String field = reportField.getField();
                if (StringUtils.isNotBlank(entity))
                    entities.add(entity);
                if (StringUtils.isNotBlank(field))
                    fields.add(field);
            }

            Map<String, Long> entityIds = entityStore.searchKeys("lookup", entities);
            Map<String, Long> fieldIds = fieldStore.searchKeys("lookup", fields);

            List<BIReportFieldModel> fieldModels = new ArrayList<BIReportFieldModel>();

            for (BIReportField reportField : reportFields) {
                Long entityId = entityIds.get(reportField.getEntity());
                Long fieldId = fieldIds.get(reportField.getField());
                BIReportFieldModel reportFieldModel = new BIReportFieldModel();

                if (entityId != null)
                    reportFieldModel.setEntity(new EntityModel(entityId));
                if (fieldId != null)
                    reportFieldModel.setField(new FieldModel(fieldId));

                reportFieldModel.setDisplayName(reportField.getDisplayName());
                reportFieldModel.setCount(reportField.getCount());
                fieldModels.add(reportFieldModel);
            }

            report.setFields(fieldModels);
        }

        report.setName(reportElement.getName());
        report.setPath(reportElement.getPath());
        report.setDescription(reportElement.getDescription());

        RegistryNodeModel parent = registryNodeStore.findByLookup(report.getPath());
        report.setParent(parent);
    }
}