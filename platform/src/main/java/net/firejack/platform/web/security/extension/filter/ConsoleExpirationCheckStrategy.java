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