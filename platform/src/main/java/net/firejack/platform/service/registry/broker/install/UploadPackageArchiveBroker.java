package net.firejack.platform.service.registry.broker.install;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.registry.domain.UploadPackageArchive;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.translate.StatusProviderTranslationResult;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.VersionUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

@TrackDetails
@Component("uploadPackageArchiveBroker")
@ProgressComponent(weight = 40, upload = true)
public class UploadPackageArchiveBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<UploadPackageArchive>> {

	public static final String PACKAGE_VERSION_HAS_INSTALLED = "PACKAGE_VERSION_HAS_INSTALLED";
	public static final String PACKAGE_VERSION_IS_ACTIVE = "PACKAGE_VERSION_IS_ACTIVE";
	public static final String PACKAGE_VERSION_EXISTS = "PACKAGE_VERSION_EXISTS";
	public static final String PACKAGE_VERSION_NOT_EXISTS = "PACKAGE_VERSION_NOT_EXISTS";

	@Autowired
	private IPackageStore packageStore;

	@Autowired
	@Qualifier("packageVersionHelper")
	private PackageVersionHelper packageVersionHelper;

	@Autowired
	private PackageInstallationService packageInstallationService;

	@Autowired
	private FileHelper helper;

	@Override
	protected ServiceResponse<UploadPackageArchive> perform(ServiceRequest<NamedValues> request) throws Exception {
		InputStream inputStream = (InputStream) request.getData().get("stream");

		UploadPackageArchive dataVO = new UploadPackageArchive();

        Long uploadFileTime = new Date().getTime();
        String randomName = SecurityHelper.generateRandomSequence(16);
        String temporaryUploadFileName = randomName + "." + uploadFileTime;

        OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,temporaryUploadFileName,inputStream, helper.getTemp());

        InputStream temporaryUploadStream = OPFEngine.FileStoreService.download( OpenFlame.FILESTORE_BASE, temporaryUploadFileName,helper.getTemp());

        ServiceResponse<FileInfo> response = OPFEngine.FileStoreService.unzipTemp(OpenFlame.FILESTORE_BASE, temporaryUploadStream, helper.getTemp());
        if (!response.isSuccess()) {
            throw new BusinessFunctionException("Package Archive has not been unzipped correctly.");
        }

        String  suffixUploadedFile = null;
        String  packageXmlUploadedFile = null;
        String  resourceZipUploadedFile = null;
        String  codeWarUploadedFile = null;

        List<FileInfo> fileInfos = response.getData();
        for (FileInfo fileInfo : fileInfos) {
            String filename = fileInfo.getFilename();
            if (filename.startsWith(PackageFileType.PACKAGE_XML.getOfrFileName())) {
                packageXmlUploadedFile = filename;
                String[] fileNameParts = filename.split("\\.");
                suffixUploadedFile = fileNameParts[2] + "." + fileNameParts[3];
            } else if (filename.startsWith(PackageFileType.RESOURCE_ZIP.getOfrFileName())) {
                resourceZipUploadedFile = filename;
            } else if (filename.startsWith(PackageFileType.APP_WAR.getOfrFileName())) {
                codeWarUploadedFile = filename;
            }
        }

        InputStream packageXml = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageXmlUploadedFile,helper.getTemp());
        IPackageDescriptor patchProcessor = packageInstallationService.getPackageDescriptor(packageXml);
        IOUtils.closeQuietly(packageXml);

        String packageLookup = patchProcessor.getPath() + "." + StringUtils.normalize(patchProcessor.getName());
        PackageModel packageRN = packageStore.findByLookup(packageLookup);
        if (packageRN == null) {

            packageXml = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageXmlUploadedFile,helper.getTemp());
            InputStream resourceZip = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, resourceZipUploadedFile,helper.getTemp());

            StatusProviderTranslationResult statusResult = packageInstallationService.activatePackage(packageXml, resourceZip);

            IOUtils.closeQuietly(packageXml);
            IOUtils.closeQuietly(resourceZip);

            if (statusResult.getResult() && statusResult.getPackage() != null) {
                packageRN = statusResult.getPackage();
                if (packageRN != null) {
                    String packageXmlName = packageRN.getName() + PackageFileType.PACKAGE_XML.getDotExtension();

                    InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageXmlUploadedFile,helper.getTemp());
                    OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,packageXmlName,stream, helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));
                    IOUtils.closeQuietly(stream);

                    if (resourceZipUploadedFile != null) {
                        String resourceZipName = packageRN.getName() + PackageFileType.RESOURCE_ZIP.getDotExtension();
                        stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, resourceZipUploadedFile,helper.getTemp());
                        OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,resourceZipName,stream, helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));
                        IOUtils.closeQuietly(stream);
                    }

                    if (codeWarUploadedFile != null) {
                        String codeWarName = packageRN.getName() + PackageFileType.APP_WAR.getDotExtension();
                        stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, codeWarUploadedFile, helper.getTemp());
                        OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,codeWarName,stream, helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));
                        IOUtils.closeQuietly(stream);
                    }

                    String packageFilename = packageRN.getName() + PackageFileType.PACKAGE_OFR.getDotExtension();
                    stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, temporaryUploadFileName, helper.getTemp());
                    OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, packageFilename, stream, helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));
                    IOUtils.closeQuietly(stream);
                } else {
                    throw new BusinessFunctionException("Package archive has not created.");
                }
            }
            dataVO.setStatus(PACKAGE_VERSION_HAS_INSTALLED);
        } else {
            dataVO.setPackageId(packageRN.getId());
            Integer uploadedVersion = VersionUtils.convertToNumber(patchProcessor.getVersion());
            if (packageRN.getVersion().equals(uploadedVersion)) {
                dataVO.setStatus(PACKAGE_VERSION_IS_ACTIVE);
            } else {
                if (packageVersionHelper.existsVersion(packageRN.getId(), uploadedVersion)) {
                    dataVO.setStatus(PACKAGE_VERSION_EXISTS);
                } else {
                    dataVO.setStatus(PACKAGE_VERSION_NOT_EXISTS);
                }
            }
            dataVO.setVersionName(patchProcessor.getVersion());
            dataVO.setUploadedFilename(suffixUploadedFile);
        }

		return new ServiceResponse<UploadPackageArchive>(dataVO, "Package archive has uploaded successfully.", true);
	}
}
