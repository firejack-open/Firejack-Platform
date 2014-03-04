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

package net.firejack.platform.web.security.oauth.client;

import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.oauth.provider.OAuthProcessor;
import net.oauth.*;
import net.oauth.client.OAuthClient;
import net.oauth.client.URLConnectionClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 *
 */
@SuppressWarnings("unused")
public class OpenFlameOAuthClient {

    private OAuthAccessor accessor;
    private OAuthClient client;
    private OAuthServiceProvider oAuthServiceProvider;

    /**
     * @param consumerKey
     * @param baseOpenFlameUrl
     * @param callbackUrl
     */
    public OpenFlameOAuthClient(String consumerKey, String baseOpenFlameUrl, String callbackUrl) {
        if (StringUtils.isBlank(consumerKey)) {
            throw new IllegalArgumentException("Consumer Key parameter should not be blank.");
        } else if (StringUtils.isBlank(baseOpenFlameUrl)) {
            throw new IllegalArgumentException("Open Flame Base url parameter should not be blank.");
        }
        callbackUrl = StringUtils.isBlank(callbackUrl) ? "none" : callbackUrl;
        this.oAuthServiceProvider = new OAuthServiceProvider(baseOpenFlameUrl + "/oauth-provider/request_token",
                baseOpenFlameUrl + "/oauth-provider/authorize", baseOpenFlameUrl + "/oauth-provider/access_token");
        OAuthConsumer consumer = OAuthProcessor.populateConsumer(consumerKey, callbackUrl, oAuthServiceProvider);
        this.accessor = new OAuthAccessor(consumer);
        this.client = new OAuthClient(new URLConnectionClient());
    }

    /**
     * @param consumerKey
     * @param baseOpenFlameUrl
     */
    public OpenFlameOAuthClient(String consumerKey, String baseOpenFlameUrl) {
        this(consumerKey, baseOpenFlameUrl, null);
    }

    /**
     * @param userName
     * @param password
     * @throws net.oauth.OAuthException
     */
    public void authorize(String userName, String password) throws OAuthException {
        //obtain request token first
        try {
            this.client.getRequestToken(accessor);
        } catch (IOException e) {
            throw new OAuthException(e);
        } catch (URISyntaxException e) {
            throw new OAuthException(e);
        }
        //then check Open Flame authentication
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("login", userName);
        paramsMap.put("password", password);
        paramsMap.put(OAuth.OAUTH_TOKEN, accessor.requestToken);
        paramsMap.put(OpenFlameSecurityConstants.OAUTH_CALLBACK_PARAMETER, "none");

        List<Map.Entry> parameters = new ArrayList<Map.Entry>(paramsMap.entrySet());
        try {
            OAuthMessage responseMessage = client.invoke(accessor, HTTPMethod.POST.name(),
                    oAuthServiceProvider.userAuthorizationURL, parameters);
            System.out.println(responseMessage.readBodyAsString());
            //logger.info("Response from authorization:");
            //logger.info(responseMessage.readBodyAsString());
            client.getAccessToken(accessor, HTTPMethod.GET.name(), null);
        } catch (IOException e) {
            throw new OAuthException(e);
        } catch (URISyntaxException e) {
            throw new OAuthException(e);
        }
    }

    /**
     * @param httpMethod
     * @param url
     * @param parameters
     * @return
     * @throws net.oauth.OAuthException
     */
    public OAuthMessage execute(String httpMethod, String url, Map<String, String> parameters) throws OAuthException {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("Service url should not be blank.");
        }
        HTTPMethod method = HTTPMethod.findByName(httpMethod);
        return execute(method, url, parameters);
    }

    /**
     * @param httpMethod
     * @param url
     * @param parameters
     * @return
     * @throws net.oauth.OAuthException
     */
    public OAuthMessage execute(HTTPMethod httpMethod, String url, Map<String, String> parameters) throws OAuthException {
        if (httpMethod == null) {
            //logger.info("Unrecognizable http method passed as input parameter. Using default method (GET).");
            httpMethod = HTTPMethod.GET;
        }
        Collection<Map.Entry<String, String>> params = parameters == null || parameters.isEmpty() ? null :
                new ArrayList<Map.Entry<String, String>>(parameters.entrySet());
        try {
            OAuthMessage request = this.accessor.newRequestMessage(httpMethod.name(), url, params);
            return this.client.invoke(request, ParameterStyle.AUTHORIZATION_HEADER);
        } catch (IOException e) {
            throw new OAuthException(e);
        } catch (URISyntaxException e) {
            throw new OAuthException(e);
        }
    }

    /**
     * @param httpMethod
     * @param url
     * @param parameters
     * @return
     * @throws net.oauth.OAuthException
     */
    public String executeWithStringResponse(String httpMethod, String url, Map<String, String> parameters) throws OAuthException {
        OAuthMessage responseMessage = execute(httpMethod, url, parameters);
        try {
            return responseMessage.readBodyAsString();
        } catch (IOException e) {
            throw new OAuthException(e);
        }
    }

    /**
     * @param httpMethod
     * @param url
     * @param parameters
     * @return
     * @throws net.oauth.OAuthException
     */
    public String executeWithStringResponse(HTTPMethod httpMethod, String url, Map<String, String> parameters) throws OAuthException {
        OAuthMessage responseMessage = execute(httpMethod, url, parameters);
        try {
            return responseMessage.readBodyAsString();
        } catch (IOException e) {
            throw new OAuthException(e);
        }
    }
}