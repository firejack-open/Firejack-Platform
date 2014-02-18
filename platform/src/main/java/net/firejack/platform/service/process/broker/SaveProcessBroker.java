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

package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.store.registry.IProcessStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 * Class encapsulates the functionality of persisting a process
 */
public abstract class SaveProcessBroker
        extends OPFSaveBroker<ProcessModel, Process, Process> {

    @Autowired
    @Qualifier("processStore")
    private IProcessStore processStore;
    @Autowired
    private TaskCaseProcessor taskCaseProcessor;

    private ThreadLocal<Boolean> strategyHolder = new ThreadLocal<Boolean>();

    /**
     * Converts a process data transfer object to a process entity
     * @param process process data transfer object
     * @return process entity
     */
    @Override
    protected ProcessModel convertToEntity(Process process) {
        strategyHolder.set(process.getSupportMultiActivities());
        return factory.convertFrom(ProcessModel.class, process);
    }

    /**
     * Converts a process entity to a process data transfer object
     * @param entity process entity
     * @return process data transfer object
     */
    @Override
    protected Process convertToModel(ProcessModel entity) {
        Process process = factory.convertTo(Process.class, entity);
        Boolean multiBranchStrategy = strategyHolder.get();
        strategyHolder.remove();
        process.setSupportMultiActivities(multiBranchStrategy);
        taskCaseProcessor.saveProcessCaseStrategy(process);
        return process;
    }

    /**
     * Invokes data access layer in order to save a process
     *
     * @param processModel process entity
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected void save(ProcessModel processModel) throws Exception {
        processStore.save(processModel);
    }

}