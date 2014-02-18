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

package net.firejack.platform.service.content.broker.collection;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.content.ImportContentProcessor;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Component("importCollectionArchiveFileBroker")
@TrackDetails
//@ProgressComponent(upload = true)
public class ImportCollectionArchiveFileBroker
		extends ServiceBroker<ServiceRequest<NamedValues<InputStream>>, ServiceResponse> {

	@Autowired
	private FileHelper helper;

	@Autowired
	@Qualifier("importContentProcessor")
	private ImportContentProcessor importContentProcessor;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues<InputStream>> request) throws Exception {
		InputStream inputStream = request.getData().get("inputStream");

		try {
			Long uploadFileTime = new Date().getTime();
			String randomName = SecurityHelper.generateRandomSequence(16);
			String temporaryUploadFileName = randomName + "." + uploadFileTime;
			OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,temporaryUploadFileName,inputStream, helper.getTemp());


			String contentXmlUploadedFile = null;
			String resourceZipUploadedFile = null;

			ZipInputStream zipFile = new ZipInputStream(OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE,temporaryUploadFileName,helper.getTemp()));
			try {
				ZipEntry entry;
				while ((entry = zipFile.getNextEntry()) != null) {
					if (PackageFileType.CONTENT_XML.getOfrFileName().equals(entry.getName())) {
						contentXmlUploadedFile = PackageFileType.CONTENT_XML.name() + randomName + "." + uploadFileTime;
						OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,contentXmlUploadedFile,zipFile, helper.getTemp());
					} else if (PackageFileType.RESOURCE_ZIP.getOfrFileName().equals(entry.getName())) {
						resourceZipUploadedFile = PackageFileType.RESOURCE_ZIP.name() + randomName + "." + uploadFileTime;
						OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,resourceZipUploadedFile,zipFile, helper.getTemp());
					}
				}
			} catch (IOException e) {
				throw new BusinessFunctionException(e.getMessage());
			} finally {
				zipFile.close();
			}

			InputStream contentXml = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, contentXmlUploadedFile,helper.getTemp());
			InputStream resourceZip = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, resourceZipUploadedFile,helper.getTemp());
			importContentProcessor.importContent(contentXml, resourceZip);
			IOUtils.closeQuietly(contentXml);
			IOUtils.closeQuietly(resourceZip);
		} catch (IOException e) {
			throw new BusinessFunctionException(e.getMessage());
		} catch (JAXBException e) {
			throw new BusinessFunctionException(e.getMessage());
		}
		return new ServiceResponse("Content Archive has uploaded successfully.", true);
	}

}
