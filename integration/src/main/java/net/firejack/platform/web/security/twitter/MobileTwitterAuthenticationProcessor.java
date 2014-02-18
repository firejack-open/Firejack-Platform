/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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