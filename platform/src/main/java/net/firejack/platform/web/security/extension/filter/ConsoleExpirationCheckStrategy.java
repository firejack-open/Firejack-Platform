/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.web.security.extension.filter;

import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.session.ISessionExpirationCheckStrategy;
import net.firejack.platform.web.security.session.UserSessionManager;


@Deprecated
public class ConsoleExpirationCheckStrategy implements ISessionExpirationCheckStrategy {

    @Override
    public boolean isSessionExpired(String sessionToken) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        boolean result;
        if (sessionManager.isUserSessionExpired(sessionToken)) {
            result = true;//signal that session expired
        } else {
            sessionManager.prolongUserSessionExpiration(
                    sessionToken,  Long.parseLong(OpenFlameConfig.SESSION_EXPIRATION_PERIOD.getValue()));
            result = false;
        }
        return result;
    }

    @Override
    public boolean isSessionTokenActive(String sessionToken) {
        Boolean sessionTokenActiveStatus = CacheManager.getInstance().getSessionTokenActiveStatus(sessionToken);
        return sessionTokenActiveStatus == null ? false : sessionTokenActiveStatus;
    }

}