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