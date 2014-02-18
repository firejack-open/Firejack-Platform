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

package net.firejack.platform.service.registry.helper;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.registry.domain.PackageVersion;
import net.firejack.platform.api.registry.domain.PackageVersionFile;
import net.firejack.platform.api.registry.domain.PackageVersionInfo;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.config.upgrader.SchemaUpgrader;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.VersionUtils;
import net.firejack.platform.model.helper.FileHelper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("packageVersionHelper")
public class PackageVersionHelper {
    @Autowired
    private FileHelper helper;

    public static FileInfo[] sortingByNameLikeNumber(FileInfo[] files, boolean desc) {
        Arrays.sort(files, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                Integer i1 = Integer.parseInt(((FileInfo) o1).getFilename());
                Integer i2 = Integer.parseInt(((FileInfo) o2).getFilename());
                return i1.compareTo(i2);
            }
        });
        if (desc) {
            org.apache.commons.lang.ArrayUtils.reverse(files);
        }
        return files;
    }

    public static FileInfo[] sortingByName(FileInfo[] files, boolean desc) {
        Arrays.sort(files, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                String s1 = ((FileInfo) o1).getFilename().toLowerCase();
                String s2 = ((FileInfo) o2).getFilename().toLowerCase();
                return s1.compareTo(s2);
            }
        });
        if (desc) {
            org.apache.commons.lang.ArrayUtils.reverse(files);
        }
        return files;
    }

    /**
     * @param pkg
     * @param version
     * @param xml
     * @throws java.io.IOException
     */
    public void archiveVersion(PackageModel pkg, Integer version, String xml) throws IOException {
        for (PackageFileType type : PackageFileType.values()) {
            String name = pkg.getName() + type.getDotExtension();
            InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, name, helper.getVersion(), String.valueOf(pkg.getId()), String.valueOf(pkg.getVersion()));
            if (stream != null)
                OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, name, stream, helper.getVersion(), String.valueOf(pkg.getId()), String.valueOf(version));
        }

        String name = pkg.getName() + PackageFileType.PACKAGE_XML.getDotExtension();
        InputStream stream = IOUtils.toInputStream(xml);
        OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, name, stream, helper.getVersion(), String.valueOf(pkg.getId()), version.toString());
        IOUtils.closeQuietly(stream);
    }

    /**
     * @param packageRN
     * @return
     */
    public PackageVersion populatePackageVersion(PackageModel packageRN) throws IOException {
        PackageVersion version = new PackageVersion();
        version.setPackageId(packageRN.getId());
        version.setVersion(VersionUtils.convertToVersion(packageRN.getVersion()));

        Date created = packageRN.getCreated();
        if (created == null) {
            created = new Date();
        }
        version.setCreated(created.getTime());
        version.setFileVOs(new ArrayList<PackageVersionFile>());

        String packageVersionDir = FileUtils.construct(helper.getVersion(), packageRN.getId().toString(), packageRN.getVersion().toString());
        if (packageVersionDir != null && packageRN.getUrlPath() != null) {

            List<PackageVersionFile> files = new ArrayList<PackageVersionFile>();
            for (PackageFileType type : PackageFileType.values()) {
                FileInfo info = OPFEngine.FileStoreService.getInfo(OpenFlame.FILESTORE_BASE, packageVersionDir, packageRN.getName() + type.getDotExtension()).getItem();
                if (info != null) {
                    files.add(new PackageVersionFile(info.getFilename(), info.getUpdated(), type));
                }
            }

            version.setFileVOs(files);

            List<PackageVersionFile> upgradeFileVOs = new ArrayList<PackageVersionFile>();
            ServiceResponse<FileInfo> search = OPFEngine.FileStoreService.search(OpenFlame.FILESTORE_BASE, SchemaUpgrader.UPGRADE_SCRIPT_PATTERN, packageVersionDir);
            List<FileInfo> data = search.getData();
            FileInfo[] upgradeFiles = data.toArray(new FileInfo[data.size()]);
            upgradeFiles = sortingByName(upgradeFiles, false);
            for (FileInfo upgradeFile : upgradeFiles) {
                PackageVersionFile upgradeFileVO = new PackageVersionFile(upgradeFile.getFilename(), upgradeFile.getUpdated(), PackageFileType.PACKAGE_UPGRADE);
                upgradeFileVOs.add(upgradeFileVO);
            }
            version.setUpgradeFileVOs(upgradeFileVOs);
        }
        version.setTotal(version.getFileVOs().size());
        return version;
    }

    /**
     * @param packageRN
     * @return
     */
    public List<PackageVersionInfo> populatePackageVersionInfo(PackageModel packageRN) throws IOException {
        List<PackageVersionInfo> packageVersionInfos = new ArrayList<PackageVersionInfo>();
        ServiceResponse<FileInfo> search = OPFEngine.FileStoreService.search(OpenFlame.FILESTORE_BASE, "\\d+", helper.getVersion(), packageRN.getId().toString());
        List<FileInfo> data = search.getData();
        FileInfo[] packageVersionFolders = data.toArray(new FileInfo[data.size()]);

        packageVersionFolders = sortingByNameLikeNumber(packageVersionFolders, false);
        for (FileInfo folder : packageVersionFolders) {
            String versionNumber = folder.getFilename();
            Integer version = Integer.parseInt(versionNumber);
            String versionName = VersionUtils.convertToVersion(version);
            PackageVersionInfo packageVersionInfo = new PackageVersionInfo(versionName, version);
            packageVersionInfo.setCurrent(version.equals(packageRN.getVersion()));
            packageVersionInfos.add(packageVersionInfo);
        }
        return packageVersionInfos;
    }

    /**
     * @param packageRN
     * @return
     */
    public List<PackageVersionInfo> populateUpgradeVersionInfo(PackageModel packageRN) throws IOException {
        List<PackageVersionInfo> packageVersionInfos = new ArrayList<PackageVersionInfo>();

        ServiceResponse<FileInfo> search = OPFEngine.FileStoreService.search(OpenFlame.FILESTORE_BASE, SchemaUpgrader.UPGRADE_SCRIPT_PATTERN, helper.getVersion(), packageRN.getId().toString(), packageRN.getVersion().toString());
        List<FileInfo> data = search.getData();
        FileInfo[] upgradeXmlFiles = data.toArray(new FileInfo[data.size()]);

        Pattern pattern = Pattern.compile(SchemaUpgrader.UPGRADE_SCRIPT_PATTERN);
        upgradeXmlFiles = sortingByName(upgradeXmlFiles, false);
        for (FileInfo upgradeXmlFile : upgradeXmlFiles) {
            String upgradeXmlFileName = upgradeXmlFile.getFilename();
            Matcher m = pattern.matcher(upgradeXmlFileName);
            if (m.find()) {
                String fromVersionName = m.group(1);
//                    String toVersionName = m.group(2);
                Integer fromVersion = VersionUtils.convertToNumber(fromVersionName);
                PackageVersionInfo packageVersionInfo = new PackageVersionInfo(fromVersionName, fromVersion);
                packageVersionInfos.add(packageVersionInfo);
            }
        }

        return packageVersionInfos;
    }

    /**
     * @param packageId
     * @param version
     * @return
     */
    public boolean existsVersion(Long packageId, Integer version) {
        return OPFEngine.FileStoreService.getInfo(OpenFlame.FILESTORE_BASE, helper.getVersion(), packageId.toString(), version.toString()) != null;
    }

    /**
     * @param packageId
     * @return
     * @throws java.io.IOException
     */
    public Integer getMaxVersion(Long packageId) throws IOException {
        Integer maxVersion = 0;
        ServiceResponse<FileInfo> search = OPFEngine.FileStoreService.search(OpenFlame.FILESTORE_BASE, "\\d+", helper.getVersion(), packageId.toString());
        FileInfo[] packageVersionFolders = search.getData().toArray(new FileInfo[search.getData().size()]);
        if (packageVersionFolders.length > 0) {
            packageVersionFolders = sortingByNameLikeNumber(packageVersionFolders, true);
            maxVersion = Integer.parseInt(packageVersionFolders[0].getFilename());
        }
        return maxVersion;
    }

    /**
     * @param packageId
     * @param version
     */
    public void remove(Long packageId, Integer version) {
        OPFEngine.FileStoreService.deleteDirectory(OpenFlame.FILESTORE_BASE, helper.getVersion(), packageId.toString(), version.toString());
    }

    /**
     * @param stream
     * @param version
     */
    public InputStream changeVersionNumber(InputStream stream, Integer version) throws IOException {
        String versionName = VersionUtils.convertToVersion(version);
        String packageXml = IOUtils.toString(stream);
        packageXml = packageXml.replaceAll("(<package[^<>]*?version\\s*=\\s*\\\")\\d+\\.\\d+\\.\\d+(\\\")", "$1" + versionName + "$2");
        return IOUtils.toInputStream(packageXml);
    }
}
