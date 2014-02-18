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

package net.firejack.oauth.client;

import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.web.security.oauth.client.OpenFlameOAuthClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 *
 */
public class OpenFlameOAuthClientSample {

    private static final Logger logger = Logger.getLogger(OpenFlameOAuthClientSample.class);

    private static final String BASE_URL = "http://localhost/platform";

    /**
     * @param args
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, URISyntaxException, OAuthException {
        //SystemConfig openFlameAccessConfig = ConfigManager.getOpenFlameAccessConfig();
        OpenFlameOAuthClient oAuthClientSample = new OpenFlameOAuthClient("test2", BASE_URL);
        //oAuthClientSample.authorize(openFlameAccessConfig.getUsername(), openFlameAccessConfig.getPassword());
        oAuthClientSample.authorize("admin", "111111");
        String result = oAuthClientSample.executeWithStringResponse(HTTPMethod.GET, BASE_URL + "/rest/registry/root_domain", null);
        logger.info(result);
    }
}