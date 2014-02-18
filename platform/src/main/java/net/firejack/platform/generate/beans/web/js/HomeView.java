/**
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
package net.firejack.platform.generate.beans.web.js;

import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.beans.annotation.Properties;

import java.util.Collection;

@Properties(subpackage = "view", suffix = "View", extension = ".js")
public class HomeView extends EntityView {
    private Collection<DomainView> domains;

    public HomeView(String projectPath, String name, Collection<DomainView> domains) {
        this.projectPath = projectPath;
        this.name = StringUtils.capitalize(name);
        this.normalize = name;
        this.classPath = "";
        this.domains = domains;
        this.lookup = DiffUtils.lookup(projectPath, name);
    }

    public Collection<DomainView> getDomains() {
        return domains;
    }
}
