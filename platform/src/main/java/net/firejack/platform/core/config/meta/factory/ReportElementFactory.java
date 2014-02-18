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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.config.meta.construct.ReportElement;
import net.firejack.platform.core.config.meta.construct.ReportField;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.report.ReportFieldModel;
import net.firejack.platform.core.model.registry.report.ReportModel;
import net.firejack.platform.core.store.registry.IFieldStore;
import net.firejack.platform.core.store.registry.IRelationshipStore;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


public class ReportElementFactory extends PackageDescriptorConfigElementFactory<ReportModel, ReportElement> {

    @Autowired
    private IRelationshipStore relationshipStore;
    @Autowired
    private IFieldStore fieldStore;

    public ReportElementFactory() {
        setElementClass(ReportElement.class);
        setEntityClass(ReportModel.class);
    }

    @Override
    protected void initDescriptorElementSpecific(ReportElement reportElement, ReportModel report) {
        super.initDescriptorElementSpecific(reportElement, report);
        List<ReportFieldModel> fields = report.getFields();
        if (Hibernate.isInitialized(fields)) {
            List<ReportField> reportFields = new ArrayList<ReportField>(fields.size());
            for (ReportFieldModel field : fields) {
                ReportField element = new ReportField();
                List<RelationshipModel> relationships = field.getRelationships();
                if (relationships != null) {
                    List<Reference> references = new ArrayList<Reference>(reportFields.size());
                    for (RelationshipModel relationship : relationships)
                        references.add(new Reference(relationship.getName(), relationship.getPath()));
                    element.setRelationships(references);
                }
                FieldModel fieldModel = field.getField();
                element.setField(fieldModel.getLookup());
                element.setDisplayName(field.getDisplayName());
                element.setVisible(field.getVisible());
                element.setSearchable(field.getSearchable());

                reportFields.add(element);
            }
            reportElement.setFields(reportFields);
        }
        reportElement.setName(report.getName());
        reportElement.setPath(report.getPath());
        reportElement.setDescription(report.getDescription());
    }

    @Override
    protected void initEntitySpecific(ReportModel report, ReportElement reportElement) {
        super.initEntitySpecific(report, reportElement);
        List<ReportField> reportFields = reportElement.getFields();
        if (reportFields != null) {
            Set<String> relationships = new HashSet<String>(reportFields.size());
            Set<String> fields = new HashSet<String>(reportFields.size());

            for (ReportField reportField : reportFields) {
                List<Reference> references = reportField.getRelationships();
                String field = reportField.getField();
                if (references != null)
                    for (Reference reference : references) {
                        relationships.add(DiffUtils.lookup(reference.getRefPath(), reference.getRefName()));
                    }
                if (StringUtils.isNotBlank(field))
                    fields.add(field);
            }

            Map<String, Long> relationshipIds = relationshipStore.searchKeys("lookup", relationships);
            Map<String, Long> fieldIds = fieldStore.searchKeys("lookup", fields);

            List<ReportFieldModel> fieldModels = new ArrayList<ReportFieldModel>();

            for (ReportField reportField : reportFields) {
                List<Reference> references = reportField.getRelationships();
                Long fieldId = fieldIds.get(reportField.getField());

                ReportFieldModel reportFieldModel = new ReportFieldModel();

                if (references != null){
                    List<RelationshipModel> relationshipModels = new ArrayList<RelationshipModel>();
                    for (Reference reference : references) {
                        String lookup = DiffUtils.lookup(reference.getRefPath(), reference.getRefName());
                        Long relationshipId = relationshipIds.get(lookup);
                        if (relationshipId == null)
                            throw new IllegalStateException("Can't find relationship :" + lookup + " for report: " + reportElement.getName());
                        relationshipModels.add(new RelationshipModel(relationshipId));
                    }
                    reportFieldModel.setRelationships(relationshipModels);
                }
                if (fieldId != null)
                    reportFieldModel.setField(new FieldModel(fieldId));

                reportFieldModel.setDisplayName(reportField.getDisplayName());
                reportFieldModel.setVisible(reportField.isVisible());
                reportFieldModel.setSearchable(reportField.isSearchable());
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