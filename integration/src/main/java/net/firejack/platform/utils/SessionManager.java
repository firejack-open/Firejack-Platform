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