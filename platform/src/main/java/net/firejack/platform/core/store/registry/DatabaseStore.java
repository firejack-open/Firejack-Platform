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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class provides access to the data for the registry nodes of the database type
 */
@Component("databaseStore")
public class DatabaseStore extends AliasableStore<DatabaseModel> implements IDatabaseStore {

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(DatabaseModel.class);
    }

	@Transactional(readOnly = true)
	public List<DatabaseModel> findAllDataSources(PackageModel model){
		SystemModel system = model.getSystem();
		if (system != null) {
			List<Object[]> databases = super.findByQuery(null, null, "DataBase.findAllDataSources", "lookupPrefix", model.getLookup() + ".%");
			DatabaseModel database = model.getDatabase();
			if (database != null) {
				database = findById(database.getId());
				databases.add(new Object[]{database, model});
			}

			List<DatabaseModel> models = new ArrayList<DatabaseModel>();
			for (Object[] objects : databases) {
				DatabaseModel databaseModel = (DatabaseModel) objects[0];
				RegistryNodeModel registryNodeModel = (RegistryNodeModel) objects[1];
                String lookup = registryNodeModel.getLookup();
				if (models.contains(databaseModel)) {
					DatabaseModel dm = models.get(models.indexOf(databaseModel));
					dm.addDomain(lookup);
				} else {
					databaseModel.setDomains(lookup);
					models.add(databaseModel);
				}
			}

			return models;
		}
		return Collections.emptyList();
	}

    @Override
    public List<DatabaseModel> findAllNotAssociatedDatabases() {
        return super.findByQuery(null, null, "DataBase.findAllNotAssociatedDatabases");
    }

}
