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

package net.firejack.platform.core.validation.condition;

import net.firejack.platform.api.registry.domain.Database;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.IDatabaseAssociated;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.store.registry.IDatabaseStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.Factory;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component("databaseMethodCondition")
public class DatabaseMethodCondition extends AbstractCondition {

    private static final Logger logger = Logger.getLogger(DatabaseMethodCondition.class);

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Autowired
    @Qualifier("databaseStore")
    private IDatabaseStore databaseStore;

    @Autowired
    private Factory factory;

    @Override
    public List<Constraint> generateConstraints(Map<String, String> params) {
        return null;
    }

    @Override
    public String defaultValue(Map<String, String> params) {
        String defaultValue = null;
        if (params != null) {
            String sParentId = params.get("parentId");
            if (StringUtils.isNumeric(sParentId)) {
                Long registryNodeId = Long.parseLong(sParentId);
                List<Long> registryNodeIds = new ArrayList<Long>();
                List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
                registryNodeStore.findCollectionParentIds(registryNodeIds, registryNodeId, collectionArrayIds);
                for (Long parentId : registryNodeIds) {
                    RegistryNodeModel registryNode = registryNodeStore.findById(parentId);
                    if (registryNode instanceof IDatabaseAssociated) {
                        IDatabaseAssociated databaseAssociatedModel = (IDatabaseAssociated) registryNode;
                        if (databaseAssociatedModel.getDatabase() != null) {
                            Long databaseId = databaseAssociatedModel.getDatabase().getId();
                            if (databaseId != null) {
                                DatabaseModel databaseModel = databaseStore.findById(databaseId);
                                Database database = factory.convertTo(Database.class, databaseModel);
                                try {
                                    ObjectMapper mapper = new ObjectMapper();
                                    defaultValue = mapper.writeValueAsString(database);
                                } catch (IOException e) {
                                    logger.error(e.getMessage(), e);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return defaultValue;
    }

}
