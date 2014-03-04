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