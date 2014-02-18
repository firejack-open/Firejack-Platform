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