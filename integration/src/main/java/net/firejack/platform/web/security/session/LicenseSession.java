package net.firejack.platform.web.security.session;
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


import net.firejack.platform.api.registry.domain.License;
import net.firejack.platform.web.security.license.LicenceHelper;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.model.principal.UserType;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public final class LicenseSession {

    private static final LicenseSession instance = new LicenseSession();
    private static final int DEFAULT_MAX_ALLOW_SESSION = 3;
    private static final int TTL = 60000;
    private final Map<Long, Long> sessions = new ConcurrentHashMap<Long, Long>();
    private Integer maxSession = DEFAULT_MAX_ALLOW_SESSION;


    private LicenseSession() {
        checkLicense();
    }

    public static LicenseSession getInstance() {
        return instance;
    }

    public final boolean verify(OpenFlamePrincipal principal) {
        if (maxSession == null || principal.getType() != UserType.USER)
            return true;

        Long userId = principal.getUserInfoProvider().getId();
        if (sessions.size() >= maxSession && !sessions.containsKey(userId)) {
            return false;
        } else {
            sessions.put(userId, System.currentTimeMillis() + TTL);
        }

        return true;
    }

    public final void remove(OpenFlamePrincipal principal) {
        if (maxSession == null || principal.getType() != UserType.USER)
            return;

        Long userId = principal.getUserInfoProvider().getId();
        sessions.remove(userId);
    }

    private void checkLicense() {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    License license = LicenceHelper.read();
                    if (license != null) {
                        LicenceHelper.verify(license);
                        maxSession = license.getSession();
                        cancel();
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e.getMessage());
                }
            }
        }, 0, TTL);
    }
}
