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