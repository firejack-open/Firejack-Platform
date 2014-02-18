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

package net.firejack.platform.web.security.ws.authorization;

import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import org.apache.cxf.security.SecurityContext;
import org.apache.log4j.Logger;

public class OpenFlameWSSecurityContext implements SecurityContext {

    private static final Logger logger = Logger.getLogger(OpenFlameWSSecurityContext.class);

    @Override
    public OpenFlamePrincipal getUserPrincipal() {
        OpenFlamePrincipal principal;
        try {
            OPFContext context = OPFContext.getContext();
            principal = context.getPrincipal();
        } catch (ContextLookupException e) {
            logger.error(e.getMessage(), e);
            principal = null;
        }
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

}