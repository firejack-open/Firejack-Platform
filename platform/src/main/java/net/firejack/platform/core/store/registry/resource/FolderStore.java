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

package net.firejack.platform.core.store.registry.resource;

import net.firejack.platform.core.model.UID;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.resource.FolderModel;
import net.firejack.platform.core.store.registry.RegistryNodeStore;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;


/**
 * Class provides access to the data for the registry nodes of the folder type
 */
@Component("folderStore")
public class FolderStore extends RegistryNodeStore<FolderModel> implements IFolderStore {

    public static final String DEFAULT_DOC_FOLDER_NAME = "opf-doc";
    public static final String DEFAULT_ACTION_PARAMETERS_FOLDER_NAME = "parameters";

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(FolderModel.class);
    }

    @Override
    @Transactional
    public FolderModel findOrCreateFolder(RegistryNodeModel registryNode) {
        String folderLookup = registryNode.getLookup() + "." + DEFAULT_DOC_FOLDER_NAME;
        FolderModel folder = findByLookup(folderLookup);
        if (folder == null) {
            folder = new FolderModel();
            folder.setName(DEFAULT_DOC_FOLDER_NAME);
            folder.setParent(registryNode);
            save(folder);
        }
        return folder;
    }

    @Override
    @Transactional
    public FolderModel findOrCreateActionFolder(ActionModel actionModel) {
        String actionFolderLookup = actionModel.getLookup() + "." + DEFAULT_DOC_FOLDER_NAME;
        FolderModel folder = findByLookup(actionFolderLookup);
        if (folder == null) {
            folder = new FolderModel();
            folder.setName(DEFAULT_DOC_FOLDER_NAME);
            folder.setParent(actionModel);
            save(folder);

            FolderModel parametersDocsFolder = new FolderModel();
            parametersDocsFolder.setName(DEFAULT_ACTION_PARAMETERS_FOLDER_NAME);
            parametersDocsFolder.setParent(actionModel);
            save(parametersDocsFolder);
        }
        return folder;
    }

    @Override
    public FolderModel findOrCreateActionParametersFolder(ActionModel actionModel) {
        FolderModel opfDocFolder = findOrCreateActionFolder(actionModel);
        return findByLookup(opfDocFolder.getLookup() + '.' + DEFAULT_ACTION_PARAMETERS_FOLDER_NAME);
    }

    @Override
    @Transactional
    public void saveForGenerator(FolderModel folder) {
        //as soon as folder automatically could be created by other store classes we have to check
        //if there is a folder with the same lookup already exist in the database.
        FolderModel oldFolder = findByLookup(folder.getLookup());
        if (oldFolder == null) {
            super.saveForGenerator(folder);
        } else {
            Hibernate.initialize(oldFolder.getUid());
            if (!folder.getUid().getUid().equals(oldFolder.getUid().getUid())) {
                logger.error("Folder with the same lookup already exist and having different uid value.");
                UID foundUid = uidById(folder.getUid().getUid());
                if (foundUid == null) {
                    foundUid = folder.getUid();
                    getHibernateTemplate().saveOrUpdate(foundUid);
                }
                oldFolder.setUid(foundUid);
            }
            oldFolder.setDescription(folder.getDescription());
            super.saveOrUpdate(oldFolder);
        }
    }

}