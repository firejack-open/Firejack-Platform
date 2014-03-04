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

package net.firejack.platform.web.security.oauth.provider;

import net.firejack.platform.core.utils.StringUtils;
import net.oauth.OAuthMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

public class OAuthRequest {

    private HttpServletRequest httpRequest;
    private OAuthMessage message;

    /**
     * @param httpRequest
     * @param message
     */
    public OAuthRequest(HttpServletRequest httpRequest, OAuthMessage message) {
        this.httpRequest = httpRequest;
        this.message = message;
    }

    /**
     * @return
     */
    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    /**
     * @return
     */
    public OAuthMessage getMessage() {
        return message;
    }

	/**
	 *
	 * @return
	 */
    public Map<String, String[]> getHttpParameters() {
        return getHttpRequest() == null ? Collections.<String, String[]>emptyMap() :
                (Map<String, String[]>) getHttpRequest().getParameterMap();
    }

    /**
     * @param key
     * @return
     */
    public String getHttpParameter(String key) {
        return getHttpRequest() == null || StringUtils.isBlank(key) ? null : getHttpRequest().getParameter(key);
    }
}