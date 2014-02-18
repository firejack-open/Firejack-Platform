/**
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
