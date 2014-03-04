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
