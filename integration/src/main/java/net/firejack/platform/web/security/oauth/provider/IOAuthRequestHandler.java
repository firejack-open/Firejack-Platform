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

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IOAuthRequestHandler {

    /**
     * @return
     */
    String getUrlEntryPoint();

    /**
     * @param httpRequest
     * @param httpResponse
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    void processPOST(OAuthRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException;

    /**
     * @param httpRequest
     * @param httpResponse
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    void processGET(OAuthRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException;

    /**
     * @param filterConfig
     * @throws javax.servlet.ServletException
     */
    void init(FilterConfig filterConfig) throws ServletException;

}