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
