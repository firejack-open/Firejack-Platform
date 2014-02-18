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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.element.directory.GroupElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import net.firejack.platform.core.store.registry.IDirectoryStore;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class GroupElementFactory extends PackageDescriptorConfigElementFactory<GroupModel, GroupElement> {

    @Autowired
    @Qualifier("directoryStore")
    private IDirectoryStore directoryStore;

    public GroupElementFactory() {
        setElementClass(GroupElement.class);
        setEntityClass(GroupModel.class);
    }

    @Override
    protected void initDescriptorElementSpecific(GroupElement groupElement, GroupModel group) {
        super.initDescriptorElementSpecific(groupElement, group);
        if (group.getDirectory() != null) {
            if (Hibernate.isInitialized(group.getDirectory())) {
                groupElement.setDirectoryRef(group.getDirectory().getLookup());
                groupElement.setPath(null);
            }
        } else if (group.getParent() != null && Hibernate.isInitialized(group.getParent()) &&
                group.getParent().getType() == RegistryNodeType.DIRECTORY) {
            groupElement.setDirectoryRef(group.getParent().getLookup());
        }
    }

    @Override
    protected void initEntitySpecific(GroupModel group, GroupElement groupElement) {
        super.initEntitySpecific(group, groupElement);

        if (StringUtils.isNotBlank(groupElement.getDirectoryRef())) {
            DirectoryModel directory = directoryStore.findByLookup(groupElement.getDirectoryRef());
            if (directory == null) {
                throw new BusinessFunctionException(
                        "No such directory found by lookup = '" + groupElement.getDirectoryRef() + "'");
            }
            group.setDirectory(directory);
            group.setPath(groupElement.getDirectoryRef());
            group.setLookup(DiffUtils.lookup(group.getPath(), group.getName()));
            group.setParent(directory);
        } else if (group.getParent() != null && group.getParent().getType() == RegistryNodeType.DIRECTORY) {
            group.setDirectory((DirectoryModel) group.getParent());
        }
    }

}