/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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
