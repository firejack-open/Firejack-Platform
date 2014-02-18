package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.broker.ReadAllBroker;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.store.lookup.ILookupStore;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

@TrackDetails
@Component("readAllProcessTreeBroker")
public class ReadAllProcessTreeBroker extends ReadAllBroker<ProcessModel, RegistryNodeTree> {

	@Autowired
	private IProcessStore store;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected ILookupStore<ProcessModel, Long> getStore() {
		return store;
	}

    protected List<RegistryNodeTree> convertToDTOs(List<ProcessModel> processModels) {
        List<RegistryNodeTree> nodeList = new ArrayList<RegistryNodeTree>();
        for (RegistryNodeModel model : processModels) {
            RegistryNodeTree node = treeNodeFactory.convertTo(model);
            node.setLeaf(true);
            nodeList.add(node);
        }
        return nodeList;
    }
}
