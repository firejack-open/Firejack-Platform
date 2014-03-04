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


package net.firejack.platform.service.registry.broker.package_;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.PackageVersion;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.context.ConfigContextFactory;
import net.firejack.platform.core.config.meta.context.IUpgradeConfigContext;
import net.firejack.platform.core.config.meta.parse.DescriptorParserFactory;
import net.firejack.platform.core.config.meta.parse.DescriptorParserType;
import net.firejack.platform.core.config.meta.parse.IPackageDescriptorParser;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageChangesStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.generate.beans.web.api.Api;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.service.*;
import net.firejack.platform.generate.structure.Structure;
import net.firejack.platform.generate.tools.Render;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.service.registry.broker.package_.version.GeneratePackageXmlBroker;
import net.firejack.platform.service.registry.helper.IGatewayService;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TrackDetails
@Component("generateWarBroker")
@ProgressComponent(weight = 90)
public class GenerateWarBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<PackageVersion>> {
	@Autowired
	private GeneratePackageXmlBroker generatePackageXmlBroker;
	@Autowired
	private IDomainGeneratorService beanGeneratorService;
	@Autowired
	private IModelGeneratorService domainGeneratorService;
	@Autowired
	private IStoreGeneratorService storeGeneratorService;
	@Autowired
	private IBrokerGeneratorService brokerGeneratorService;
	@Autowired
	private IAPIGeneratorService apiGeneratorService;
	@Autowired
	private IEndpointGenerationService endpointGenerationService;
	@Autowired
	private IResourceGeneratorService resourceGeneratorService;
	@Autowired
	private IScriptGeneratorService scriptGeneratorService;
	@Autowired
	private IDatabaseSQLGeneratorService databaseSQLGeneratorService;
	@Autowired
	private IPadGeneratorService iPadGeneratorService;
	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private PackageVersionHelper packageVersionHelper;
	@Autowired
	private FileHelper helper;
	@Autowired
	private IGatewayService gatewayService;
    @Autowired
    private IPackageChangesStore changesStore;

    private ThreadLocal<Map<String,Object>> cache = new ThreadLocal<Map<String, Object>>();

	@Autowired
	private Render render;

	@Override
	protected ServiceResponse<PackageVersion> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
		Long packageId = request.getData().getIdentifier();

		ServiceResponse<PackageVersion> response;
		Structure structure = null;
		try {
            cache.set(new HashMap<String, Object>());
            gatewayService.setCache(cache);
            domainGeneratorService.setCache(cache);
            beanGeneratorService.setCache(cache);
            storeGeneratorService.setCache(cache);
            brokerGeneratorService.setCache(cache);
            apiGeneratorService.setCache(cache);
            scriptGeneratorService.setCache(cache);

            gatewayService.prepareGateway(packageId);

			generatePackageXmlBroker.execute(request);
			PackageModel packageModel = packageStore.findById(packageId);
			String url = WebUtils.getNormalizedUrl(packageModel.getServerName(), packageModel.getPort(), packageModel.getUrlPath());

			String name = packageModel.getName() + PackageFileType.PACKAGE_XML.getDotExtension();
			InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, name, helper.getVersion(), packageModel.getId().toString(), packageModel.getVersion().toString());

			ConfigContextFactory configContextFactory = ConfigContextFactory.getInstance();
			IUpgradeConfigContext<InputStream> context = configContextFactory.buildContext(stream);

			IPackageDescriptorParser<InputStream> xmlMetaDataParser = DescriptorParserFactory.getInstance().buildMetaDataParser(DescriptorParserType.XML_STREAM);
			IPackageDescriptor descriptor = xmlMetaDataParser.parsePackageDescriptor(context);
			IOUtils.closeQuietly(stream);
			structure = resourceGeneratorService.createTempProject(descriptor);

            List<Model> models = domainGeneratorService.generateModels(descriptor, structure);
			beanGeneratorService.generateDomains(models, structure);
			storeGeneratorService.generateStoreService(models, structure);
			brokerGeneratorService.generateBrokerService(descriptor, models, structure);

			Api api = apiGeneratorService.generateAPIService(descriptor, models, url, structure);

			endpointGenerationService.generationEndpoint(descriptor, api, structure);
			scriptGeneratorService.generateScript(api, structure);
			scriptGeneratorService.generateWizard(descriptor, structure);
			databaseSQLGeneratorService.generateSqlScript(descriptor, structure);

			resourceGeneratorService.generateResource(descriptor, api, structure, packageModel);
			resourceGeneratorService.unPackageResources(structure);

            name = packageModel.getName() + PackageFileType.RESOURCE_ZIP.getDotExtension();
    		stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, name, helper.getVersion(), packageModel.getId().toString(), packageModel.getVersion().toString());
            resourceGeneratorService.copyJar(descriptor, stream, structure);
            IOUtils.closeQuietly(stream);

			iPadGeneratorService.prepareStructure(structure);
			iPadGeneratorService.generateModel(api, structure);

			resourceGeneratorService.buildMavenProject(structure, "clean package -DskipTests=true", packageModel);
			resourceGeneratorService.generateUpgrade(packageModel);
			resourceGeneratorService.generateOFR(packageModel);

            changesStore.cleanPackageChange(packageId);

			PackageVersion packageVersionVO = packageVersionHelper.populatePackageVersion(packageModel);
			response = new ServiceResponse<PackageVersion>(packageVersionVO, "Generate war successfully", true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response = new ServiceResponse<PackageVersion>("Generate war fail", false);
		} finally {
			if (structure != null)
                structure.clean();
			render.clean();
            cache.get().clear();
            cache.remove();
        }
		return response;
	}
}
