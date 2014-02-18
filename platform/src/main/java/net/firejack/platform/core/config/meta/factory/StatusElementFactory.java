/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.config.meta.factory;


import net.firejack.platform.core.config.meta.element.process.StatusElement;
import net.firejack.platform.core.model.registry.process.StatusModel;
import net.firejack.platform.core.store.registry.IProcessStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class StatusElementFactory extends
        PackageDescriptorConfigElementFactory<StatusModel, StatusElement> {

    @Autowired
    @Qualifier("processStore")
    protected IProcessStore processStore;

    /***/
    public StatusElementFactory() {
        setElementClass(StatusElement.class);
        setEntityClass(StatusModel.class);
    }

    @Override
    protected void initEntitySpecific(StatusModel status, StatusElement statusElement) {
        super.initEntitySpecific(status, statusElement);
        status.setSortPosition(statusElement.getOrder());
        /*status.setName(statusElement.getName());
        status.setDescription(statusElement.getDescription());
        if (StringUtils.isNotBlank(statusElement.getPath())) {
            status.setPath(DiffUtils.lookupByRefPath(statusElement.getPath()));
            ProcessModel parent = processStore.findByLookup(status.getPath());
            status.setParent(parent);
        }
        status.setLookup(DiffUtils.lookup(status.getPath(), status.getName()));
        status.set*/
    }

    @Override
    protected void initDescriptorElementSpecific(StatusElement statusElement, StatusModel status) {
        super.initDescriptorElementSpecific(statusElement, status);
        statusElement.setOrder(status.getSortPosition());
        /*statusElement.setDescription(status.getDescription());
        statusElement.setName(status.getName());*/
    }
}