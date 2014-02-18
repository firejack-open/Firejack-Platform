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
