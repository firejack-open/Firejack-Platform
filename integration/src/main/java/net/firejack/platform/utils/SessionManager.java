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

package net.firejack.platform.utils;

import net.firejack.platform.core.model.user.IUserInfoProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class SessionManager {

    private static Log logger = LogFactory.getLog(SessionManager.class);

    public static final String SESSION_USER_INFO_PARAM = "SESSION_USER_INFO_PARAM";

    private static SessionManager instance;

    private ConcurrentMap<String, HttpSession> sessionsMap = new ConcurrentHashMap<String, HttpSession>();

    /**
     * @param token
     */
    public void logout(String token) {
        logger.debug("logout: token=" + token);
        HttpSession session = sessionsMap.remove(token);
        if (session != null) {
            session.invalidate();
            logger.debug("logout invalidate: token=" + token);
        }
    }

    /**
     * @param token
     */
    public void cleanSession(String token) {
        HttpSession session = sessionsMap.remove(token);
        if (session != null) {
            session.removeAttribute(SESSION_USER_INFO_PARAM);
        }
    }

    /**
     * @param httpSession
     */
    public void addSession(HttpSession httpSession) {
        sessionsMap.putIfAbsent(httpSession.getId(), httpSession);
    }

    /**
     * @param user
     * @param httpSession
     */
    public void addUserToSession(IUserInfoProvider user, HttpSession httpSession) {
        HttpSession session = sessionsMap.get(httpSession.getId());
        if (session == null) {
            session = httpSession;
            sessionsMap.putIfAbsent(httpSession.getId(), session);
        }
        session.setAttribute(SESSION_USER_INFO_PARAM, user);
    }

    /**
     * @return
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

}