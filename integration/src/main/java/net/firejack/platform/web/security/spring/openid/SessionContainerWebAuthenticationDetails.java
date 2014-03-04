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

package net.firejack.platform.web.security.spring.openid;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class SessionContainerWebAuthenticationDetails extends WebAuthenticationDetails {

    private static final long serialVersionUID = -3310160602394390140L;

    private HttpSession session;

    /**
     * Records the remote address and will also set the session Id if a session
     * already exists (it won't create one).
     *
     * @param request that the authentication request was received from
     */
    public SessionContainerWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.session = request.getSession();
    }

    /**
     * @return
     */
    public HttpSession getSession() {
        return session;
    }

    /**
     * @param session
     */
    public void setSession(HttpSession session) {
        this.session = session;
    }

}