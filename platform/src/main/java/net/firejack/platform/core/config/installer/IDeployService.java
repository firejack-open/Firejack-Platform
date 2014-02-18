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

package net.firejack.platform.core.config.installer;

import net.firejack.platform.api.deployment.domain.Deployed;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.api.registry.domain.DatabaseAction;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IDeployService {

	/**
	 * @param packageRN
	 * @param token
	 * @param stream
	 * @param associatedDatabases
	 */
	void deployPackage(PackageModel packageRN, String token, InputStream stream, Map<String, DatabaseAction> associatedDatabases);

 	void undeployPackage(PackageModel packageRN, String token, Map<String, DatabaseAction> associatedDatabases);

	/**
	 *
	 *
	 *
	 * @param system
	 *
	 * @return
	 */
	List<Deployed> getDeployedPackageNames(SystemModel system);

	List<WebArchive> getWarStatus(Long systemId);

	void restart();
}
