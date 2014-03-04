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

package net.firejack.platform.api.registry.listener;

import net.firejack.platform.api.Elements;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFServiceTests;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.domain.RootDomain;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.response.ServiceResponse;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.Map;

public class RootDomainExecutionListener extends AbstractTestExecutionListener {

    public static final String ROOT_DOMAIN_NAME = "testrootdomain.ru";

    private static final Logger logger = Logger.getLogger(RootDomainExecutionListener.class);

    private Map<Elements, AbstractDTO> testContextAttributes;

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        testContextAttributes = (Map<Elements, AbstractDTO>) testContext.getApplicationContext().getBean("testContextAttributes");
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
		RootDomain rootDomain = new RootDomain();
		rootDomain.setName(ROOT_DOMAIN_NAME);

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createRootDomain(rootDomain);
		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create root domain response null.", response);
		Assert.assertNotNull("Can't be create root domain item null.", response.getItem());

		rootDomain.setId(response.getItem().getId());

        testContextAttributes.put(Elements.ROOT_DOMAIN, rootDomain);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
	    OPFEngine.init(OPFServiceTests.ADMIN_LOGIN, OPFServiceTests.ADMIN_PASSWORD);
        RootDomain rootDomain = (RootDomain) testContextAttributes.get(Elements.ROOT_DOMAIN);
        ServiceResponse response = OPFEngine.RegistryService.deleteRootDomain(rootDomain.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("RootDomain should be deleted.", response.isSuccess());
    }

}
