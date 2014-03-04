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

package net.firejack.platform.api;

import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.InstallUtils;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.x509.KeyUtils;
import org.apache.log4j.Logger;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.io.File;
import java.util.Map;

public class OPFEngineInitializeExecutionListener extends AbstractTestExecutionListener {

    private static final Logger logger = Logger.getLogger(OPFEngineInitializeExecutionListener.class);

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        logger.info("Configuring stsBaseUrl Environment Variable.");
	    File keyStore = InstallUtils.getKeyStore();

	    Map<String, String> props = KeyUtils.getMapProperties(Env.getDefaultEnvFile(), keyStore);
	    ConfigContainer.putAll(props);

	    String domainUrl = OpenFlameConfig.DOMAIN_URL.getValue();
	    String port = OpenFlameConfig.PORT.getValue();
	    String contextUrl = OpenFlameConfig.CONTEXT_URL.getValue();
	    if (domainUrl != null && port != null && contextUrl != null) {
            String stsBaseUrl = WebUtils.getNormalizedUrl(domainUrl, port, contextUrl);
            Env.FIREJACK_URL.setValue(stsBaseUrl);
//            OpenFlameSecurityConstants.setSTSBaseUrl(stsBaseUrl);
	    }
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
         OPFEngine.init(OPFServiceTests.ADMIN_LOGIN, OPFServiceTests.ADMIN_PASSWORD);
    }
}
