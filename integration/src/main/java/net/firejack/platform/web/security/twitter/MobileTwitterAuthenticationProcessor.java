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

package net.firejack.platform.web.security.twitter;

import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;


public class MobileTwitterAuthenticationProcessor extends BaseTwitterAuthenticationProcessor {

    private static final Logger logger = Logger.getLogger(MobileTwitterAuthenticationProcessor.class);
    private static final String PARAM_ACCESS_TOKEN = "access_token";
    private static final String PARAM_ACCESS_TOKEN_SECRET = "access_token_secret";

    @Override
    public void processAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        ServiceResponse serviceResponse;
        if (StringUtils.isBlank(getTwitterConsumerKey()) || StringUtils.isBlank(getTwitterConsumerSecret())) {
            logger.error("Twitter consumer key or consumer secret configs were not set.");
            serviceResponse = new ServiceResponse(
                    "Twitter consumer key or consumer secret configs were not set.", false);
        } else {
            String accessToken = request.getParameter(PARAM_ACCESS_TOKEN);
            String accessTokenSecret = request.getParameter(PARAM_ACCESS_TOKEN_SECRET);
            if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(accessTokenSecret)) {
                serviceResponse = new ServiceResponse(
                        PARAM_ACCESS_TOKEN + " and " + PARAM_ACCESS_TOKEN_SECRET +
                        " parameters should be provided.", false);
            } else {
                AccessToken token = new AccessToken(accessToken, accessTokenSecret);
                if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(accessTokenSecret)) {
                    logger.warn("accessToken = " + accessToken + "; accessSecret = " + accessTokenSecret);
                }
                try {
                    Twitter twitter = populateTwitterService();
                    twitter.setOAuthAccessToken(token);
                    String browserIpAddress = request.getRemoteAddr();
                    AuthenticationToken authenticationToken = getOpenFlameToken(twitter, browserIpAddress);

                    if (authenticationToken == null) {
                        logger.error("Authentication token is null.");
                        serviceResponse = new ServiceResponse("Authentication token is null.", false);
                    } else {
                        serviceResponse = new ServiceResponse(
                                authenticationToken, "Authentication token obtained successfully.", true);
                    }
                } catch (Throwable th) {
                    logger.error(th.getMessage(), th);
                    serviceResponse = new ServiceResponse(
                            th.getMessage() == null ? "Server error occurred." : th.getMessage(), false);
                }
            }
        }
        try {
            processMobilePhoneResponse(serviceResponse, request, response);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processUnAuthentication(HttpServletRequest request, HttpServletResponse response) {
    }

    private void processMobilePhoneResponse(
            Object respObject, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String requestType = request.getContentType() == null ?
                request.getHeader("accept") : request.getContentType();
        if (MediaType.APPLICATION_XML.equalsIgnoreCase(requestType)) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance();
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                ServletOutputStream out = response.getOutputStream();
                marshaller.marshal(respObject, out);
                out.close();
            } catch (JAXBException e) {
                logger.error(e.getMessage(), e);
                OpenFlameSecurityConstants.printXmlErrorToResponse(
                        response, OpenFlameSecurityConstants.API_SECURITY_ERROR_RESPONSE.format(
                        new String[]{e.getMessage()}));

            }
        } else {
            String jsonData;
            try {
                jsonData = WebUtils.serializeObjectToJSON(respObject);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                jsonData = "{ success: false; message: '" + e.getMessage() + "' }";
            }
            ServletOutputStream out = response.getOutputStream();
            out.println(jsonData);
            out.close();
        }
    }
}