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

import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.wizard.WizardFieldModel;
import net.firejack.platform.core.model.registry.wizard.WizardModel;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class provides access to the data for the registry nodes of the database type
 */
@Component("wizardStore")
public class WizardStore extends RegistryNodeStore<WizardModel> implements IWizardStore {

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IActionStore actionStore;

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(WizardModel.class);
    }

    @Override
    @Transactional
    public void saveForGenerator(WizardModel wizard) {
        List<WizardFieldModel> wizardFieldModels = wizard.getFields();
        for (WizardFieldModel wizardFieldModel : wizardFieldModels) {
            wizardFieldModel.setWizard(wizard);
        }
        wizard.setFields(null);

        EntityModel entityModel = entityStore.findById(wizard.getMain().getId());

        wizard.setServerName(entityModel.getServerName());
        wizard.setPort(entityModel.getPort());
        wizard.setParentPath(entityModel.getParentPath());
        wizard.setUrlPath(entityModel.getUrlPath() + "/" + StringUtils.normalize(wizard.getName()));
        wizard.setProtocol(entityModel.getProtocol());
        wizard.setStatus(entityModel.getStatus());

        super.save(wizard);

        for (WizardFieldModel wizardFieldModel : wizardFieldModels) {
            getHibernateTemplate().save(wizardFieldModel);
        }
    }

    @Override
    @Transactional
    public void save(WizardModel wizard) {
        List<WizardFieldModel> wizardFieldModels = wizard.getFields();

        boolean valid = false;
        for (WizardFieldModel wizardFieldModel : wizardFieldModels) {
            wizardFieldModel.setWizard(wizard);
            if (wizardFieldModel.getForm() != null)
                valid = true;
        }

        if (!valid)
            throw new BusinessFunctionException("Can't create Wizard without fields.");

        EntityModel entityModel = entityStore.findById(wizard.getMain().getId());

        wizard.setServerName(entityModel.getServerName());
        wizard.setPort(entityModel.getPort());
        wizard.setParentPath(entityModel.getParentPath());
        wizard.setUrlPath(entityModel.getUrlPath() + "/" + StringUtils.normalize(wizard.getName()));
        wizard.setProtocol(entityModel.getProtocol());
        wizard.setStatus(entityModel.getStatus());

        super.save(wizard);
    }

    @Transactional(readOnly = true)
    public List<WizardModel> findAllByLikeLookupPrefix(final String lookupPrefix) {
        List<WizardModel> models = super.findAllByLikeLookupPrefix(lookupPrefix);
        for (WizardModel model : models) {
            Hibernate.initialize(model.getMain());
            List<WizardFieldModel> fields = model.getFields();
            Hibernate.initialize(fields);
            if (fields != null) {
                for (WizardFieldModel field : fields) {
                    Hibernate.initialize(field.getField());
                    Hibernate.initialize(field.getRelationship());
                }
            }

        }
        return models;
    }

    @Override
    @Transactional
    public void deleteAllByRegistryNodeId(Long id) {
        List<WizardModel> models = findAllByParentIdWithFilter(id, null);
        super.deleteAll(models);

    }

    @Override
    public void delete(WizardModel wizardModel) {
        if (getHibernateTemplate().contains(wizardModel)) {
            List<WizardFieldModel> fields = wizardModel.getFields();
            if (fields != null && Hibernate.isInitialized(fields)) {
                Collections.sort(fields, new Comparator<WizardFieldModel>() {
                    @Override
                    public int compare(WizardFieldModel o1, WizardFieldModel o2) {
                        return o2.getForm() == null ? -1 : 1;
                    }
                });
            }
            super.delete(wizardModel);
        }
    }
}
