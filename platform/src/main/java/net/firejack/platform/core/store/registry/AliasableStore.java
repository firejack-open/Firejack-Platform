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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.utils.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class AliasableStore<R extends RegistryNodeModel> extends RegistryNodeStore<R> implements IAliasableStore<R> {

    @Autowired
    @Qualifier("systemStore")
    private ISystemStore systemStore;

    @Override
	@Transactional
	public void save(R model) {
        super.save(model);

        RegistryNodeModel mainSystemModel = model.getParent();
        List<SystemModel> aliasSystemModels = systemStore.findAliasesById(mainSystemModel.getId(), null);
        for (SystemModel aliasSystemModel : aliasSystemModels) {
            synchronize(mainSystemModel, aliasSystemModel);
        }
    }

    @Override
    @Transactional
    public void delete(R model) {
        List<R> aliasModels = findAliasesById(model.getId(), null);
        for (R aliasModel : aliasModels) {
            deleteRecursively(aliasModel);
        }
        super.delete(model);
    }

    public void synchronize(RegistryNodeModel mainSystemModel, SystemModel aliasSystemModel) {
        List<R> mainServerModels = findAllByParentIdWithFilter(mainSystemModel.getId(), null);
        for (R mainServerModel : mainServerModels) {
            R serverModel = findByParentIdAndMainId(aliasSystemModel.getId(), mainServerModel.getId());
            if (serverModel == null) {
                serverModel = instantiate();
            }
            ClassUtils.copyProperties(mainServerModel, serverModel, new String[]{"id", "path", "lookup", "parent", "uid", "hash"});
            serverModel.setParent(aliasSystemModel);
            serverModel.setMain(mainServerModel);
            save(serverModel);
        }
    }

}
