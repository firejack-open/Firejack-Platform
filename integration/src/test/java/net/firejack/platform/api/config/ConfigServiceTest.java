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

package net.firejack.platform.api.config;


import net.firejack.platform.api.Elements;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.config.model.ConfigType;
import net.firejack.platform.api.registry.RegistryServiceTest;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Env;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.EnumMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class
})
public class ConfigServiceTest {
	protected static Logger logger = Logger.getLogger(ConfigServiceTest.class);

	@Value("${sts.base.url}")
	private String baseUrl;

	private static EnumMap<Elements, Lookup> map;

	@Before
	public void setUp() {
		Env.FIREJACK_URL.setValue(baseUrl);

		OPFEngine.initialize();
		if (map == null) {
			map = RegistryServiceTest.createStandardTree();
		}
	}

	@After
	public void tearDown() {
	}

	@AfterClass
	public static void tearDownClass() {
		OPFEngine.RegistryService.deleteRootDomain(map.get(Elements.ROOT_DOMAIN).getId());
	}

	@Test
	public void crudConfig() {
		Config config = new Config();
		config.setParentId(map.get(Elements.SYSTEM).getId());
		config.setName("testconfig");
		config.setValue("123");

		ServiceResponse<Config> response = OPFEngine.ConfigService.createConfig(config);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create response null.", response);
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<Config> serviceResponse = OPFEngine.ConfigService.readConfig(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		config = serviceResponse.getItem();
		config.setName("testconfig2");

		response = OPFEngine.ConfigService.updateConfig(config.getId(), config);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());

		serviceResponse = OPFEngine.ConfigService.findByLookup(serviceResponse.getItem().getPath(), ConfigType.SYSTEM);

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.ConfigService.readAllConfigsByRegistryNodeId(map.get(Elements.SYSTEM).getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.ConfigService.searchConfig(map.get(Elements.SYSTEM).getId(), "test");

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.ConfigService.deleteConfig(config.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}
}
