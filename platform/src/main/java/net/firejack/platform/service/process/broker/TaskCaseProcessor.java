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

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.config.model.ConfigType;
import net.firejack.platform.api.process.domain.Case;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class TaskCaseProcessor {

    private static final String SMA_CONFIG_NAME = "processes-with-multi-activities";

    @Autowired
    @Qualifier("packageStore")
    private IPackageStore packageStore;

    private static final Logger logger = Logger.getLogger(TaskCaseProcessor.class);

    public void saveProcessCaseStrategy(Process process) {
        boolean isNew = process.getId() == null;
        boolean supportMultiActivities = process.getSupportMultiActivities() != null && process.getSupportMultiActivities();
        if ((isNew && supportMultiActivities) || !isNew) {
            String processLookup = DiffUtils.lookup(process.getPath(), process.getName());
            String configLookup = getConfigLookup(process.getLookup());
            ServiceResponse<Config> response = OPFEngine.ConfigService.findByLookup(configLookup, ConfigType.STRING);

            Config config = null;
            if (response.isSuccess() && ((config = response.getItem()) != null)) {
                String[] processLookupList = config.getValue().split(";");
                if (processLookupList.length > 0) {
                    Set<String> lookupList = new HashSet<String>(Arrays.asList(processLookupList));
                    if (supportMultiActivities) {
                        lookupList.add(processLookup);
                    } else {
                        lookupList.remove(processLookup);
                    }
                    String configValue = lookupList.size() == 0 ? "" :
                            lookupList.size() == 1 ? lookupList.iterator().next() :
                                    StringUtils.join(lookupList, ';');
                    config.setValue(configValue);
                } else if (supportMultiActivities) {
                    config.setValue(processLookup);
                } else {
                    config = null;
                }
            } else if (supportMultiActivities) {
                config = new Config();
                config.setName(SMA_CONFIG_NAME);
                config.setPath(StringUtils.getPackageLookup(processLookup));
                config.setLookup(configLookup);
                config.setValue(processLookup);
                PackageModel packageModel = packageStore.findByLookup(
                        StringUtils.getPackageLookup(process.getPath()));
                config.setParentId(packageModel.getId());
            }
            if (config != null) {
                saveConfig(config);
            }
        }
    }

    public void initializeCaseStrategy(Process process) {
        if (process != null) {
            Boolean multiActivitySupported = getMultiBranchStrategy(process.getLookup());
            process.setSupportMultiActivities(multiActivitySupported);
        }
    }

    public Boolean getMultiBranchStrategy(String processLookup) {
        String configLookup = getConfigLookup(processLookup);
        ServiceResponse<Config> response = OPFEngine.ConfigService.findByLookup(configLookup, ConfigType.STRING);
        Boolean multiBranchStrategy = false;
        if (response.isSuccess()) {
            Config config = response.getItem();
            if (config != null) {
                String configValue = config.getValue();
                multiBranchStrategy = configValue.contains(processLookup);
            }
        } else {
            logger.warn("Failed to read config bu lookup [" + configLookup + "]. Reason: " + response.getMessage());
        }
        return multiBranchStrategy;
    }

    public void initializeCaseStrategy(Map<String, List<Process>> processesMap) {
        for (Map.Entry<String, List<Process>> entry : processesMap.entrySet()) {
            List<Process> processList = entry.getValue();
            initializeCaseStrategy(processList.get(0));
            for (int i = 1; i < processList.size(); i++) {
                processList.get(i).setSupportMultiActivities(processList.get(0).getSupportMultiActivities());
            }
        }
    }

    public void registerTaskProcess(Task task, Map<String, List<Process>> processesMap) {
        Process process = task.getProcessCase().getProcess();
        List<Process> processes = processesMap.get(process.getLookup());
        if (processes == null) {
            processes = new ArrayList<Process>();
            processesMap.put(process.getLookup(), processes);
        }
        processes.add(process);
    }

    public void registerCaseProcess(Case processCase, Map<String, List<Process>> processesMap) {
        Process process = processCase.getProcess();
        List<Process> processes = processesMap.get(process.getLookup());
        if (processes == null) {
            processes = new ArrayList<Process>();
            processesMap.put(process.getLookup(), processes);
        }
        processes.add(process);
    }

    private void saveConfig(Config config) {
        ServiceResponse<Config> response = config.getId() == null ?
                OPFEngine.ConfigService.createConfig(config) :
                OPFEngine.ConfigService.updateConfig(config.getId(), config);
        if (!response.isSuccess()) {
            logger.error("Failed to save Multi Activity mode configuration. Reason: " + response.getMessage());
        }
    }

    private String getConfigLookup(String processLookup) {
        String processPackageLookup = StringUtils.getPackageLookup(processLookup);
        return processPackageLookup + '.' + SMA_CONFIG_NAME;
    }

}