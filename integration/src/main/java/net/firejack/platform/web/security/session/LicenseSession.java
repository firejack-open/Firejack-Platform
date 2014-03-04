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

package net.firejack.platform.web.security.session;


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
