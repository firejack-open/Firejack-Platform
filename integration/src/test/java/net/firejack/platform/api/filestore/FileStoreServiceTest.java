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

package net.firejack.platform.api.filestore;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.OpenFlame;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		OPFEngineInitializeExecutionListener.class
})
public class FileStoreServiceTest {
	protected static Logger logger = Logger.getLogger(FileStoreServiceTest.class);

	@Value("${sts.base.url}")
	private String baseUrl;

	@Test
	public void testFileStoreDirectory() {
		ServiceResponse response = OPFEngine.FileStoreService.createDirectory(OpenFlame.FILESTORE_BASE, "test");
		logger.info(response.getMessage());

		response = OPFEngine.FileStoreService.renameDirectory(OpenFlame.FILESTORE_BASE, "test1","test");
		logger.info(response.getMessage());

		response = OPFEngine.FileStoreService.search(OpenFlame.FILESTORE_BASE, ".*te.*","test");
		logger.info(response.getMessage());

		response = OPFEngine.FileStoreService.deleteDirectory(OpenFlame.FILESTORE_BASE, "test");
		logger.info(response.getMessage());
	}

	@Test
	public void testFileStore() throws IOException {
		ServiceResponse response = OPFEngine.FileStoreService.createDirectory(OpenFlame.FILESTORE_BASE, "test");
		logger.info(response.getMessage());

		InputStream stream = IOUtils.toInputStream("qwe");
		response = OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, "test.txt", stream,"test");
		logger.info(response.getMessage());
		IOUtils.closeQuietly(stream);

		stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, "test.txt","test");
		String s = IOUtils.toString(stream);
		IOUtils.closeQuietly(stream);
		logger.info(response.getMessage());
		Assert.assertEquals(s, "qwe");

		response = OPFEngine.FileStoreService.deleteFile(OpenFlame.FILESTORE_BASE, "test.txt","test");
		logger.info(response.getMessage());
		response = OPFEngine.FileStoreService.deleteDirectory(OpenFlame.FILESTORE_BASE,"test");
		logger.info(response.getMessage());

		response = OPFEngine.FileStoreService.readFileStoreInfo();
		logger.info(response.getMessage());
	}
}
