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

package net.firejack.platform.web.security.openid;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.log4j.Logger;
import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenIDConsumer {

    public static final String OPEN_ID_IDENTITY_REQUEST_PARAMETER = "openid.identity";

    private ConsumerManager consumerManager;
    private static final Logger logger = Logger.getLogger(OpenIDConsumer.class);

    /**
     * @throws org.openid4java.consumer.ConsumerException default constructor may throw ConsumerException
     *
     */
    public OpenIDConsumer() throws ConsumerException {
        this.consumerManager = new ConsumerManager();
    }

    /**
     * @param httpRequest request
     * @return true if passed request is ready, otherwise false
     */
    public boolean isResponseReady(HttpServletRequest httpRequest) {
        String identity = httpRequest.getParameter(OPEN_ID_IDENTITY_REQUEST_PARAMETER);
        return StringUtils.isNotBlank(identity);
    }

    /**
     * @param identifier openid identifier
     * @param httpRequest http request
     * @return destination url
     */
    public String processRequest(String identifier, HttpServletRequest httpRequest) {
        try {
            List discoveryItems = consumerManager.discover(identifier);
            DiscoveryInformation discoveredInfo = consumerManager.associate(discoveryItems);

            HttpSession session = httpRequest.getSession();
            session.setAttribute(DiscoveryInformation.class.getName(), discoveredInfo);
            session.setAttribute(OpenFlameSecurityConstants.CLIENT_IP_ADDRESS_ATTRIBUTE, httpRequest.getRemoteAddr());

            AuthRequest authRequest = consumerManager.authenticate(
                    discoveredInfo, WebUtils.getRequestUrl(httpRequest));

            FetchRequest fetchRequest = FetchRequest.createFetchRequest();
            SupportedOpenIDAttribute.EMAIL.append(fetchRequest);
            SupportedOpenIDAttribute.USERNAME.append(fetchRequest);
            SupportedOpenIDAttribute.FIRST_NAME.append(fetchRequest);
            SupportedOpenIDAttribute.MIDDLE_NAME.append(fetchRequest);
            SupportedOpenIDAttribute.LAST_NAME.append(fetchRequest);

            authRequest.addExtension(fetchRequest);
            return authRequest.getDestinationUrl(true);
        } catch (DiscoveryException e) {
            logger.error(e.getMessage(), e);
        } catch (ConsumerException e) {
            logger.error(e.getMessage(), e);
        } catch (MessageException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param httpRequest http request
     * @return authentication token returned by OpenFlame
     */
    public AuthenticationToken processResponse(HttpServletRequest httpRequest) {
        ParameterList response = new ParameterList(httpRequest.getParameterMap());

        // retrieve the previously stored discovery information
        HttpSession session = httpRequest.getSession();
        DiscoveryInformation discoveredInfo = (DiscoveryInformation)
                session.getAttribute(DiscoveryInformation.class.getName());
        String requestUrl = WebUtils.getRequestUrl(httpRequest);
        String queryString = httpRequest.getQueryString();

        if (StringUtils.isNotBlank(queryString)) {
            requestUrl = requestUrl + "?" + queryString;
        }
        AuthenticationToken authenticationToken = null;
        try {
            VerificationResult verificationResult = consumerManager.verify(requestUrl, response, discoveredInfo);
            Identifier verifiedIdentifier = verificationResult.getVerifiedId();
            if (verifiedIdentifier != null) {
                AuthSuccess authSuccess = (AuthSuccess) verificationResult.getAuthResponse();
                if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                    FetchResponse fetchResp = (FetchResponse)
                            authSuccess.getExtension(AxMessage.OPENID_NS_AX);

                    Map<SupportedOpenIDAttribute, String> attributeValues = new HashMap<SupportedOpenIDAttribute, String>();
                    for (SupportedOpenIDAttribute attributeSupported : SupportedOpenIDAttribute.values()) {
                        @SuppressWarnings("unchecked")
                        List<String> values = (List<String>) fetchResp.getAttributeValues(attributeSupported.getAttributeName());
                        if (values != null) {
                            for (String value : values) {
                                if (value != null) {
                                    attributeValues.put(attributeSupported, value);
                                    break;
                                }
                            }
                        }
                    }
                    String email = attributeValues.get(SupportedOpenIDAttribute.EMAIL);
                    if (email != null) {
                        authenticationToken = requestOpenIDAuthentication(email, attributeValues,
                                (String) httpRequest.getAttribute(OpenFlameSecurityConstants.CLIENT_IP_ADDRESS_ATTRIBUTE));
                    }

                }
            }
        } catch (MessageException e) {
            logger.error(e.getMessage(), e);
        } catch (DiscoveryException e) {
            logger.error(e.getMessage(), e);
        } catch (AssociationException e) {
            logger.error(e.getMessage(), e);
        }
        return authenticationToken;
    }

    protected AuthenticationToken requestOpenIDAuthentication(
            String email, Map<SupportedOpenIDAttribute, String> mappedAttributes, String ipAddress) {
        ServiceResponse<AuthenticationToken> response =
                OPFEngine.AuthorityService.processOpenIdSignIn(email, mappedAttributes, ipAddress);
        AuthenticationToken token;
        if (response == null) {
            throw new IllegalStateException("API Service response should not be null.");
        } else if (response.isSuccess()) {
            token = response.getItem();
        } else {
            logger.error("API Service response has failure status. Reason: " + response.getMessage());
            token = null;
        }
        return token;
    }

}