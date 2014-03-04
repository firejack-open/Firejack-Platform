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

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;



public class SessionListener implements HttpSessionListener {

    private SessionManager sessionManager;

    public void sessionCreated(HttpSessionEvent event) {
        if (sessionManager == null) {
            initSessionManager(event);
        }
        HttpSession session = event.getSession();
        sessionManager.addSession(session);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        sessionManager.cleanSession(session.getId());
    }

    private void initSessionManager(HttpSessionEvent event) {
        ServletContext servletContext = event.getSession().getServletContext();
        AbstractApplicationContext context = (AbstractApplicationContext)
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        sessionManager = (SessionManager) context.getBean("sessionManager");
        if (sessionManager == null) {
            throw new RuntimeException("Wrong configuration initialization: SessionManager not set");
        }
    }

}