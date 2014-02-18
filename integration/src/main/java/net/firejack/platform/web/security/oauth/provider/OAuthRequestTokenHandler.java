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

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static net.firejack.platform.web.security.model.OpenFlameSecurityConstants.*;

public class OAuthRequestTokenHandler extends BasicOAuthRequestHandler {
    private static final Logger logger = Logger.getLogger(OAuthRequestTokenHandler.class);
    public static final String DEFAULT_REQUEST_TOKEN_HANDLER_URL = "/oauth-provider/request_token";

    /**
     * @param urlEntryPoint
     */
    public OAuthRequestTokenHandler(String urlEntryPoint) {
        super(urlEntryPoint);
    }

    /***/
    public OAuthRequestTokenHandler() {
        this(DEFAULT_REQUEST_TOKEN_HANDLER_URL);
    }

    protected void processRequest(OAuthRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            OAuthConsumer consumer = OAuthProcessor.getConsumer(request.getMessage());
            OAuthAccessor accessor = new OAuthAccessor(consumer);

            validateMessage(request, accessor);
            // Support the 'Variable Accessor Secret' extension
            // described in http://oauth.pbwiki.com/AccessorSecret
            String secret = request.getMessage().getParameter(OAUTH_ACCESSOR_SECRET_PARAMETER);
            if (secret != null) {
                accessor.setProperty(OAuthConsumer.ACCESSOR_SECRET, secret);
            }
            // generate request_token and secret
            OAuthProcessor.generateRequestToken(accessor);

            response.setContentType("text/plain");
            OutputStream out = response.getOutputStream();
            OAuth.formEncode(OAuth.newList(OAUTH_TOKEN_PARAMETER, accessor.requestToken,
                    OAUTH_TOKEN_SECRET_PARAMETER, accessor.tokenSecret), out);
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            OAuthProcessor.handleException(e, request.getHttpRequest(), response, true);
        }
    }

}