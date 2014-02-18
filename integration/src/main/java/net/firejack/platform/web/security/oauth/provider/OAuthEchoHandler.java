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

package net.firejack.platform.web.security.oauth.provider;

import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.oauth.OAuthAccessor;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class OAuthEchoHandler extends BasicOAuthRequestHandler {
    private static final Logger logger = Logger.getLogger(OAuthEchoHandler.class);
    public static final String DEFAULT_ECHO_HANDLER_URL = "/oauth-provider/echo";

    /**
     * @param urlEntryPoint
     */
    public OAuthEchoHandler(String urlEntryPoint) {
        super(urlEntryPoint);
    }

    /***/
    public OAuthEchoHandler() {
        this(DEFAULT_ECHO_HANDLER_URL);
    }

    @Override
    protected void processRequest(OAuthRequest request, HttpServletResponse response) throws IOException, ServletException {
        super.processRequest(request, response);
        try {
            OAuthAccessor accessor = OAuthProcessor.getAccessor(request.getMessage());
            validateMessage(request, accessor);

            AuthenticationToken openFlameToken = (AuthenticationToken) accessor.getProperty(
                    OpenFlameSecurityConstants.OAUTH_OPF_TOKEN_ATTRIBUTE);

            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(openFlameToken == null || openFlameToken.getUser() == null ?
                    "[Your UserId is blank]" :
                    "[Your UserId:" + openFlameToken.getUser().getUsername() + "]");
            for (Map.Entry<String, String[]> parameter : request.getHttpParameters().entrySet()) {
                String[] values = parameter.getValue();
                for (String value : values) {
                    out.println(parameter.getKey() + ": " + value);
                }
            }
            out.close();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            OAuthProcessor.handleException(e, request.getHttpRequest(), response, false);
        }
    }
}