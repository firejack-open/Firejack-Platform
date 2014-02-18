package net.firejack.platform.api.deployment;
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


import net.firejack.platform.api.deployment.domain.Deployed;
import net.firejack.platform.api.deployment.domain.NavigationChanges;
import net.firejack.platform.api.deployment.domain.PackageChange;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.core.response.ServiceResponse;

public interface IDeploymentService {
	ServiceResponse deploy(Long packageId, String name, String file);
	ServiceResponse undeploy(String name);
	ServiceResponse<Deployed> list();
	ServiceResponse<WebArchive> status();
	ServiceResponse restart();

    ServiceResponse<PackageChange> changes(String  packageLookup);

    ServiceResponse<NavigationChanges> lastNavigationChanges(Long timestamp);

}
