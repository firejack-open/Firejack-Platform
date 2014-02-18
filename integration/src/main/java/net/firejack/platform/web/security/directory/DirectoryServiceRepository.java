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

package net.firejack.platform.web.security.directory;

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.security.directory.annotation.DirectoryServiceConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryServiceRepository implements ApplicationContextAware {

    private static final Logger logger = Logger.getLogger(DirectoryServiceRepository.class);

    private ApplicationContext applicationContext;
    private Map<String, DirectoryServiceMetaData> directoryServiceConfig =
            new HashMap<String, DirectoryServiceMetaData>();
    private DirectoryServiceMetaData defaults;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        String[] serviceBeanNames = applicationContext.getBeanNamesForType(IDirectoryService.class);
        for (String serviceBeanName : serviceBeanNames) {
            Class<?> serviceClass = applicationContext.getType(serviceBeanName);
            DirectoryServiceConfig serviceConfigAnnotation = serviceClass.getAnnotation(DirectoryServiceConfig.class);
            if (serviceConfigAnnotation == null) {
                logger.error("Failed to load directoryService meta data - annotation of type " +
                        DirectoryServiceConfig.class + " was not specified for bean [" + serviceBeanName + "]");
                throw new BeanCreationException("Wrong configuration of directory service [" + serviceBeanName + "]. Configuration annotation was not specified.");
            } else {
                DirectoryServiceMetaData metaData = directoryServiceConfig.get(serviceConfigAnnotation.lookup());
                if (metaData == null) {
                    metaData = new DirectoryServiceMetaData();
                    metaData.setBeanName(serviceBeanName);
                    metaData.setId(serviceConfigAnnotation.lookup());
                    metaData.setTitle(serviceConfigAnnotation.title());
                    directoryServiceConfig.put(serviceConfigAnnotation.lookup(), metaData);

                    if (serviceConfigAnnotation.defaults()) {
                        this.defaults = metaData;
                    }
                } else {
                    logger.warn("Directory service config has id = [" + metaData.getId() +
                            "] that already registered. Directory service config of bean [" + serviceBeanName +
                            "] clashes with bean [" + metaData.getId() + "]. Bean [" + metaData.getBeanName() +
                            "] will be used for particular id.");
                }
            }
        }
    }

    /**
     * @param lookup
     * @return
     */
    public IDirectoryService getDirectoryService(String lookup) {
        IDirectoryService directoryService = null;
        if (StringUtils.isNotBlank(lookup)) {
            DirectoryServiceMetaData serviceMetaData = directoryServiceConfig.get(lookup);
            if (serviceMetaData != null) {
                directoryService = this.applicationContext.getBean(serviceMetaData.getBeanName(), IDirectoryService.class);
            }
        }
        return directoryService;
    }

    /**
     * @param lookup
     * @return
     */
    public String getDirectoryServiceTitle(String lookup) {
        if (StringUtils.isNotBlank(lookup)) {
            DirectoryServiceMetaData metaData = directoryServiceConfig.get(lookup);
            if (metaData != null) {
                lookup = metaData.getTitle();
            }
        }
        return lookup;
    }

    /**
     * @return
     */
    public IDirectoryService getDefault() {
        return this.applicationContext.getBean(this.defaults.getBeanName(), IDirectoryService.class);
    }

    /**
     * @return
     */
    public List<Tuple<String, String>> getDirectoryServiceLabels() {
        List<Tuple<String, String>> serviceLabels = new ArrayList<Tuple<String, String>>();
        for (String id : directoryServiceConfig.keySet()) {
            DirectoryServiceMetaData metaData = directoryServiceConfig.get(id);
            serviceLabels.add(new Tuple<String, String>(id, metaData.getTitle()));
        }
        return serviceLabels;
    }
}