package net.firejack.platform.generate.service;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.config.meta.IDomainElement;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.element.resource.FileResourceVersionElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.generate.beans.web.api.Api;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.structure.MavenStructure;
import net.firejack.platform.generate.structure.Structure;
import net.firejack.platform.model.config.GatewayLoader;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.reverse.ReverseEngineeringService;
import net.firejack.platform.service.registry.broker.package_.GenerateUpgradeXmlBroker;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.maven.shared.invoker.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

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

@Component
public class ResourceGeneratorService extends BaseGeneratorService implements IResourceGeneratorService {
    private static final Logger logger = Logger.getLogger(ResourceGeneratorService.class);

    @Autowired
    private FileHelper helper;

    @Override
    @ProgressStatus(weight = 2, description = "Generate resource objects")
    public void generateResource(IPackageDescriptor descriptor, Api api, Structure structure, PackageModel version) throws Exception {
        Map map = new HashMap();
        Set<String> models = new TreeSet<String>();
        Set<String> domains = new TreeSet<String>();

        map.put("name", descriptor.getName());
        map.put("path", descriptor.getPath());
        map.put("warname", descriptor.getName());
        map.put("version", VersionUtils.convertToVersion(version.getVersion()));
        map.put("models", models);
        map.put("domains", domains);
        map.put("api", api);

        Collection<Model> collection = api.getModels();
        if (collection != null) {
            for (Model model : collection) {
                if (model.getClassPath() != null) {
                    models.add(model.getPackage());
                    domains.add(model.getDomain().getPackage());
                }
            }
        }

        FileUtils.forceMkdir(structure.getResource());
        FileUtils.forceMkdir(structure.getProfile());

        generateWeb(structure, descriptor);

        generator.compose("templates/code/server/configs/hibernate.vsl", map, new File(structure.getResource(), "hibernate-config.xml"), false);
        generator.compose("templates/code/server/configs/app.vsl", map, new File(structure.getResource(), "app-config.xml"), false);
        generator.compose("templates/code/server/configs/api.vsl", map, new File(structure.getResource(), "api-config.xml"), false);
        generator.compose("templates/code/server/configs/jersey.vsl", map, new File(structure.getResource(), "jersey-config.xml"), false);
        generator.compose("templates/code/server/configs/log4j.vsl", map, new File(structure.getResource(), "log4j.properties"), false);
        generator.compose("templates/code/server/configs/pom.vsl", map, new File(structure.getProject(), "pom.xml"), false);
        generator.compose("templates/code/server/configs/web.vsl", map, new File(structure.getWebInf(), "web.xml"), false);
    }


    private void generateWeb(Structure structure, IPackageDescriptor descriptor) throws Exception {
        OutputStream out = new FileOutputStream(new File(structure.getResource(), "gateway.properties"));
        Properties properties = new Properties();

        properties.setProperty(Env.FIREJACK_URL.getPropertyName(), Env.FIREJACK_URL.getValue());
        properties.setProperty(OpenFlameConfig.MC_SERVER_URL.getKey(), OpenFlameConfig.MC_SERVER_URL.getValue());
        properties.setProperty(OpenFlameConfig.MC_PORT.getKey(), OpenFlameConfig.MC_PORT.getValue());

        properties.setProperty(GatewayLoader.LOOKUP, DiffUtils.lookup(descriptor.getPath(), descriptor.getName()));
        properties.setProperty(GatewayLoader.TIMEOUT, "20000");

        properties.store(out, null);
        IOUtils.closeQuietly(out);
    }

    @ProgressStatus(weight = 2, description = "UnPackage Resources")
    public void unPackageResources(Structure structure) throws IOException {
        ArchiveUtils.unZIP(getResource("templates/code/web/resources.zip"), structure.getProject());
    }

    @ProgressStatus(weight = 2, description = "Copy jars")
    public void copyJar(IPackageDescriptor descriptor, InputStream stream, final Structure structure) throws Exception {
        ResourceElement[] resources = descriptor.getResources();
        IDomainElement[] domains = descriptor.getConfiguredDomains();

        if(domains ==  null)
            return;

        final Map<String, String> schemas = new HashMap<String, String>();
        for (IDomainElement domain : domains) {
            if (StringUtils.isNotBlank(domain.getWsdlLocation())) {
                Process exec = Runtime.getRuntime().exec(new String[]{"wsimport", "-d", structure.getSrc().getPath(), "-p", "wsdl." + StringUtils.normalize(domain.getName()), "-Xnocompile", "-target", "2.1", "-extension", domain.getWsdlLocation()});
                exec.getInputStream().close();
                exec.getErrorStream().close();
                exec.getOutputStream().close();
                exec.waitFor();
            }
        }

        for (ResourceElement resource : resources) {
            if (resource.getName().equals(ReverseEngineeringService.WSDL_SCHEME)) {
                List<FileResourceVersionElement> fileResourceVersionElements = resource.getFileResourceVersionElements();
                FileResourceVersionElement versionElement = fileResourceVersionElements.get(0);
                schemas.put(versionElement.getResourceFilename(), versionElement.getOriginalFilename());
            }
        }

        ArchiveUtils.unzip(stream, new ArchiveUtils.ArchiveCallback() {
            @Override
            public void callback(String dir, String name, InputStream stream) {
                String schemaName = schemas.get(name);
                File file = null;
                if (schemaName != null) {
                    file = FileUtils.create(structure.getResource(), "wsdl", schemaName);
                }

                if (file != null) {
                    try {
                        FileOutputStream outputStream = FileUtils.openOutputStream(file);
                        IOUtils.copy(stream, outputStream);
                        IOUtils.closeQuietly(outputStream);
                    } catch (IOException e) {
                        logger.error(e, e);
                    }
                }
            }
        });
    }

    @ProgressStatus(weight = 10, description = "Build maven project")
    public void buildMavenProject(Structure structure, String commands, PackageModel packageRN) throws Exception {
        File project = structure.getProject();
        FileUtils.writeStringToFile(new File(project, "build.bat"), "mvn " + commands);
        FileUtils.writeStringToFile(new File(project, "build.sh"), "#!/bin/sh\nmvn " + commands);

        String distDir = FileUtils.construct(helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));

        String sourceIPadName = structure.getName() + PackageFileType.IPAD_SOURCE.getDotExtension();
        String sourceName = structure.getName() + PackageFileType.PACKAGE_SOURCE.getDotExtension();
        String warName = structure.getName().replace("/", "") + PackageFileType.APP_WAR.getDotExtension();


        File ipadPath = structure.getIpad().getParentFile();
        InputStream stream = ArchiveUtils.create(ipadPath);
        if (stream != null) {
            OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, sourceIPadName, stream, distDir);
            IOUtils.closeQuietly(stream);
            FileUtils.forceDelete(ipadPath);
        }

        stream = ArchiveUtils.create(project);
        OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, sourceName, stream, distDir);
        IOUtils.closeQuietly(stream);
        File war = FileUtils.create(structure.getBuild(), structure.getName(), warName);

        InvocationRequest request = new DefaultInvocationRequest();

        request.setPomFile(new File(project, "pom.xml"));
        request.setBaseDirectory(project);
        request.setGoals(Arrays.asList(commands.split(" ")));
        request.setShowErrors(true);

        String m2_home = Env.M2_HOME.getValue();
        if (m2_home == null || m2_home.isEmpty()) {
            logger.warn("Project can not be compiled because not defined Maven Home");
            throw new Exception("Project can not be compiled because not defined Maven Home");
        }

        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            logger.error(request);
            throw new Exception("Maven  error " + result.getExitCode());
        }

        FileInputStream inputStream = FileUtils.openInputStream(war);
        try {
            OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, warName, inputStream, distDir);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    @ProgressStatus(weight = 20, description = "Generate upgrade file")
    public void generateUpgrade(PackageModel model) throws InterruptedException {
        if (model.getVersion() > model.getDatabaseVersion())
            OPFEngine.RegistryService.generateUpgradeXml(model.getId(), model.getDatabaseVersion());
    }

    @Override
    @ProgressStatus(weight = 5, description = "Create OFR file")
    public void generateOFR(PackageModel packageRN) throws IOException {
        Map<String, InputStream> filePaths = new HashMap<String, InputStream>();
        String destDir = FileUtils.construct(helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));

        String databaseVersion = VersionUtils.convertToVersion(packageRN.getDatabaseVersion());
        String version = VersionUtils.convertToVersion(packageRN.getVersion());
        String upgradeFileName = GenerateUpgradeXmlBroker.UPGRADE_FILENAME.format(new String[]{databaseVersion, version});

        filePaths.put(PackageFileType.PACKAGE_XML.getOfrFileName(), OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageRN.getName() + PackageFileType.PACKAGE_XML.getDotExtension(), destDir));
        filePaths.put(PackageFileType.APP_WAR.getOfrFileName(), OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageRN.getUrlPath().replace("/", "") + PackageFileType.APP_WAR.getDotExtension(), destDir));
        filePaths.put(PackageFileType.RESOURCE_ZIP.getOfrFileName(), OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageRN.getName() + PackageFileType.RESOURCE_ZIP.getDotExtension(), destDir));
        filePaths.put(PackageFileType.PACKAGE_UPGRADE.getOfrFileName(), OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, upgradeFileName, destDir));

        OPFEngine.FileStoreService.zip(OpenFlame.FILESTORE_BASE, filePaths, destDir, packageRN.getName() + PackageFileType.PACKAGE_OFR.getDotExtension());
    }

    @ProgressStatus(weight = 1, description = "Generate structure project")
    public Structure createTempProject(IPackageDescriptor descriptor) throws IOException {
        render.putSetting("name", descriptor.getName());
        String projectName = descriptor.getName() + "_" + SecurityHelper.generateRandomSequence(16);
        return new MavenStructure(new File(FileUtils.getTempDirectory(), projectName), descriptor.getName());
    }
}
