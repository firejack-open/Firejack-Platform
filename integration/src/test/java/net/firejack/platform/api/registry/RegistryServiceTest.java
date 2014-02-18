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

package net.firejack.platform.api.registry;


import net.firejack.platform.api.Elements;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.api.registry.domain.*;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.System;
import net.firejack.platform.api.registry.model.*;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.model.registry.DatabaseName;
import net.firejack.platform.core.model.registry.DatabaseProtocol;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.VersionUtils;
import net.firejack.platform.web.handler.APIException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class
})
public class RegistryServiceTest {
	protected static Logger logger = Logger.getLogger(RegistryServiceTest.class);

	@Value("${sts.base.url}")
	private String baseUrl;

	private static EnumMap<Elements, Lookup> map;

	@Before
	public void setUp() {
		Env.FIREJACK_URL.setValue(baseUrl);

		OPFEngine.initialize();
		if (map == null) {
			map = createStandardTree();
		}
	}

	@After
	public void tearDown() {

	}

	@AfterClass
	public static void tearDownClass() {
		OPFEngine.RegistryService.deleteRootDomain(map.get(Elements.ROOT_DOMAIN).getId());
	}

	public static EnumMap<Elements, Lookup> createStandardTree() {
		EnumMap<Elements, Lookup> map = new EnumMap<Elements, Lookup>(Elements.class);

		RootDomain rootDomain = new RootDomain();
		rootDomain.setName("root123test.ru");

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createRootDomain(rootDomain);
		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create root domain response null.", response);
		Assert.assertNotNull("Can't be create root domain item null.", response.getItem());

		rootDomain.setId(response.getItem().getId());
		map.put(Elements.ROOT_DOMAIN, rootDomain);

		Package aPackage = new Package();
		aPackage.setName("ext");
		aPackage.setUrlPath("/ext");
		aPackage.setParentId(rootDomain.getId());

		response = OPFEngine.RegistryService.createPackage(aPackage);
		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create package response null.", response);
		Assert.assertNotNull("Can't be create package item null.", response.getItem());
		aPackage.setId(response.getItem().getId());
		map.put(Elements.PACKAGE, aPackage);

		Domain domain = new Domain();
		domain.setName("extdomain");
		domain.setParentId(aPackage.getId());

		response = OPFEngine.RegistryService.createDomain(domain);
		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create domain response null.", response);
		Assert.assertNotNull("Can't be create domain item null.", response.getItem());
		domain.setId(response.getItem().getId());
		map.put(Elements.DOMAIN, domain);

		Entity entity = new Entity();
		entity.setName("Entity");
		entity.setStatus(RegistryNodeStatus.UNKNOWN);
		entity.setTypeEntity(EntityType.STANDARD.getEntityType());
		entity.setParentId(domain.getId());

		response = OPFEngine.RegistryService.createEntity(entity);
		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create entity response null.", response);
		Assert.assertNotNull("Can't be create entity item null.", response.getItem());
		entity.setId(response.getItem().getId());
		map.put(Elements.ENTITY, entity);

		Action action = new Action();
		action.setName("action");
		action.setParentId(entity.getId());
		action.setSoapMethod("readAction");
		action.setStatus(RegistryNodeStatus.UNKNOWN);
		action.setSoapUrlPath("/action");
		action.setMethod(HTTPMethod.GET);
		action.setProtocol(EntityProtocol.SOAP);
		action.setOutputVOEntity(entity);

		response = OPFEngine.RegistryService.createAction(action);
		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create action response null.", response);
		Assert.assertNotNull("Can't be create action item null.", response.getItem());
		action.setId(response.getItem().getId());
		map.put(Elements.ACTION, action);

		System system = new System();
		system.setParentId(rootDomain.getId());
		system.setName("system");
		system.setServerName("localhost");
		system.setStatus(RegistryNodeStatus.UNKNOWN);
		system.setPort(8080);

		response = OPFEngine.RegistryService.createSystem(system);
		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create system response null.", response);
		Assert.assertNotNull("Can't be create system item null.", response.getItem());
		system.setId(response.getItem().getId());
		map.put(Elements.SYSTEM, system);

		ServiceResponse response1 = OPFEngine.RegistryService.associatePackage(system.getId(), aPackage.getId());
		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be Association Package response null.", response1);

		return map;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void checkValidation() {
		Domain domain = new Domain();
		domain.setName("package");
		domain.setParentId(map.get(Elements.PACKAGE).getId());

		logger.info("check @Match");
		try {
			ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createDomain(domain);
		} catch (APIException e) {
			logger.info("check @Match");
		}
	}

	@Test
	public void getRegistryNodeChildren() {
		ServiceResponse<RegistryNodeTree> children = OPFEngine.RegistryService.getRegistryNodeChildren(map.get(Elements.ROOT_DOMAIN).getId(), PageType.DOMAIN, null);
		logger.info(children.getMessage());
		List<RegistryNodeTree> data = children.getData();
		Assert.assertNotNull("Can't be null.", data);
		Assert.assertEquals("Can't be empty.", false, data.isEmpty());
	}

	@Test
	public void getRegistryNodeWithExpandedChildren() {
		ServiceResponse<RegistryNodeTree> children = OPFEngine.RegistryService.getRegistryNodeWithExpandedByIdChildren(map.get(Elements.PACKAGE).getId(), PageType.DOMAIN);
		logger.info(children.getMessage());

		List<RegistryNodeTree> data = children.getData();
		Assert.assertNotNull("Can't be null.", data);
		Assert.assertEquals("Can't be empty.", false, data.isEmpty());
	}

	@Test
	public void moveRegistryNode() {
		MoveRegistryNodeTree moveRegistryNodeTree = new MoveRegistryNodeTree();
		moveRegistryNodeTree.setNewRegistryNodeParentId(31L);
		moveRegistryNodeTree.setOldRegistryNodeParentId(30L);
		moveRegistryNodeTree.setPosition(2);
		moveRegistryNodeTree.setRegistryNodeId(38L);

		ServiceResponse<MoveRegistryNodeTree> response = OPFEngine.RegistryService.moveRegistryNode(moveRegistryNodeTree);
		logger.info(response.getMessage());

		List<MoveRegistryNodeTree> data = response.getData();
		Assert.assertNull("Can't be null.", data);

		moveRegistryNodeTree = new MoveRegistryNodeTree();
		moveRegistryNodeTree.setNewRegistryNodeParentId(30L);
		moveRegistryNodeTree.setOldRegistryNodeParentId(31L);
		moveRegistryNodeTree.setPosition(5);
		moveRegistryNodeTree.setRegistryNodeId(38L);
		response = OPFEngine.RegistryService.moveRegistryNode(moveRegistryNodeTree);
		logger.info(response.getMessage());

		data = response.getData();
		Assert.assertNull("Can't be null.", data);
	}

	@Test
	public void getSearchResult() {
		ServiceResponse<Search> response = OPFEngine.RegistryService.getSearchResult("platform", null, null, "All", new Paging(0, 10));
		logger.info(response.getMessage());

		List<Search> data = response.getData();
		Assert.assertNotNull("Can't be null.", data);
		Assert.assertEquals("Can't be empty.", false, data.isEmpty());
	}

	@Test
	public void crudAction() {
		Action action = new Action();
		action.setName("test");
		action.setParentId(map.get(Elements.ENTITY).getId());
		action.setMethod(HTTPMethod.GET);
		action.setSoapMethod("testAction");
		action.setSoapUrlPath("/ws/testaction");
		action.setProtocol(EntityProtocol.SOAP);
		action.setStatus(RegistryNodeStatus.UNKNOWN);

		Entity entity = new Entity();
		entity.setId(38l);
		action.setOutputVOEntity(entity);

		ServiceResponse<Action> readAllResponse = OPFEngine.RegistryService.readAllActions();

		logger.info(readAllResponse.getMessage());
		Assert.assertNotNull("Can't be null.", readAllResponse.getData());
		Assert.assertEquals("Can't be empty.", false, readAllResponse.getData().isEmpty());

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createAction(action);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create response null.", response);
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<Action> serviceResponse = OPFEngine.RegistryService.readAction(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		action = serviceResponse.getItem();
		action.setName("test2");

		response = OPFEngine.RegistryService.updateAction(action.getId(), action);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());

		serviceResponse = OPFEngine.RegistryService.deleteAction(action.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}

	@Test
	public void assign() {
		ServiceResponse serviceResponse = OPFEngine.RegistryService.removeAssociationPackage(map.get(Elements.SYSTEM).getId(), map.get(Elements.PACKAGE).getId());
		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be Remove Association Package response null.", serviceResponse);

		serviceResponse = OPFEngine.RegistryService.associatePackage(map.get(Elements.SYSTEM).getId(), map.get(Elements.PACKAGE).getId());
		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be Association Package response null.", serviceResponse);

	}

	@Test
	public void crudDomain() {
		Domain domain = new Domain();
		domain.setName("test");
		domain.setParentId(map.get(Elements.PACKAGE).getId());

		ServiceResponse<Domain> readAllResponse = OPFEngine.RegistryService.readAllDomains();

		logger.info(readAllResponse.getMessage());
		Assert.assertNotNull("Can't be null.", readAllResponse.getData());
		Assert.assertEquals("Can't be empty.", false, readAllResponse.getData().isEmpty());

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createDomain(domain);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create response null.", response);
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<Domain> serviceResponse = OPFEngine.RegistryService.readDomain(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		domain = serviceResponse.getItem();
		domain.setName("test2");

		response = OPFEngine.RegistryService.updateDomain(domain.getId(), domain);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());

		serviceResponse = OPFEngine.RegistryService.deleteDomain(domain.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}

	@Test
	public void crudEntity() {
		Entity entity = new Entity();
		entity.setName("Test1");
		entity.setTypeEntity(EntityType.STANDARD.getEntityType());
		entity.setParentId(map.get(Elements.DOMAIN).getId());
		entity.setStatus(RegistryNodeStatus.UNKNOWN);

		ServiceResponse<Entity> readAllResponse = OPFEngine.RegistryService.readAllEntities("standard", map.get(Elements.ROOT_DOMAIN).getId());

		logger.info(readAllResponse.getMessage());
		Assert.assertNotNull("Can't be null.", readAllResponse.getData());
		Assert.assertEquals("Can't be empty.", false, readAllResponse.getData().isEmpty());

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createEntity(entity);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create response null.", response);
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<Entity> serviceResponse = OPFEngine.RegistryService.readEntity(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		entity = serviceResponse.getItem();
		entity.setName("Test2");

		response = OPFEngine.RegistryService.updateEntity(entity.getId(), entity);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());

		serviceResponse = OPFEngine.RegistryService.deleteEntity(entity.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}

	@Test
	public void installPackage() {
		try {
			ServiceResponse<UploadPackageArchive> response = OPFEngine.RegistryService.uploadPackageArchive(FileUtils.openInputStream(FileUtils.getResource("test.ofr")));

			logger.info(response.getMessage());
			Assert.assertNotNull("Can't be upload package response null.", response);

			UploadPackageArchive item = response.getItem();
			if (item.getVersionName() != null) {
				ServiceResponse serviceResponse = OPFEngine.RegistryService.performPackageArchive(item.getUploadedFilename(), item.getVersionName(), true, true);
				logger.info(serviceResponse.getMessage());
				Assert.assertNotNull("Can't be activate package response null.", serviceResponse);

				serviceResponse = OPFEngine.RegistryService.checkUniquePackageVersion(item.getPackageId(), item.getVersionName());
				logger.info(serviceResponse.getMessage());
				Assert.assertNotNull("Can't be check unique package response null.", serviceResponse);
			}
			ServiceResponse<RootDomain> serviceResponse = OPFEngine.RegistryService.readAllRootDomains();
			for (RootDomain domain : serviceResponse.getData()) {
				if (domain.getName().equals("test.com")) {
					OPFEngine.RegistryService.deleteRootDomain(domain.getId());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void crudPackage() {
		Package aPackage = new Package();
		aPackage.setName("test");
		aPackage.setUrlPath("/test");
		aPackage.setParentId(map.get(Elements.ROOT_DOMAIN).getId());

		ServiceResponse<Package> all = OPFEngine.RegistryService.readAllPackages();

		logger.info(all.getMessage());
		Assert.assertNotNull("Can't be null.", all.getData());
		Assert.assertEquals("Can't be empty.", false, all.getData().isEmpty());

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createPackage(aPackage);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create response null.", response);
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<Package> serviceResponse = OPFEngine.RegistryService.readPackage(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		aPackage = serviceResponse.getItem();
		aPackage.setName("test2");

		response = OPFEngine.RegistryService.updatePackage(aPackage.getId(), aPackage);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());

		ServiceResponse<PackageVersion> archive = OPFEngine.RegistryService.archive(aPackage.getId());
		logger.info(archive.getMessage());
		Assert.assertNotNull("Can't be archive response null.", archive);

		ServiceResponse<PackageVersionInfo> versionsInfo = OPFEngine.RegistryService.getPackageVersionsInfo(aPackage.getId());
		logger.info(versionsInfo.getMessage());
		Assert.assertNotNull("Can't be versionsInfo response null.", versionsInfo);
		Assert.assertNotNull("Can't be versionsInfo items response null.", versionsInfo.getData());

		PackageVersionInfo versionInfo = versionsInfo.getData().get(versionsInfo.getData().size() - 1);
		versionInfo.setVersionName(VersionUtils.convertToVersion(versionInfo.getVersion() + 1));
		ServiceResponse<Package> lockVersion = OPFEngine.RegistryService.lockPackageVersion(aPackage.getId(), versionInfo);

		logger.info(lockVersion.getMessage());
		Assert.assertNotNull("Can't be lock response null.", lockVersion);
		Assert.assertNotNull("Can't be lock items response null.", lockVersion.getData());

		int version = VersionUtils.convertToNumber(archive.getItem().getVersion()) - 1;
		ServiceResponse<PackageVersion> versionServiceResponse = OPFEngine.RegistryService.generateUpgradeXml(aPackage.getId(), version);
		logger.info(versionServiceResponse.getMessage());
		Assert.assertNotNull("Can't be generate upgrade response null.", versionServiceResponse);

		ServiceResponse<Package> activateResponse = OPFEngine.RegistryService.activatePackageVersion(aPackage.getId(), version);
		logger.info(activateResponse.getMessage());
		Assert.assertNotNull("Can't be activate response null.", activateResponse);

		ServiceResponse<Package> supportResponse = OPFEngine.RegistryService.supportPackageVersion(aPackage.getId(), version);
		logger.info(supportResponse.getMessage());
		Assert.assertNotNull("Can't be support response null.", supportResponse);

		ServiceResponse<Package> deleteResponse = OPFEngine.RegistryService.deletePackageVersion(aPackage.getId(), version);
		logger.info(deleteResponse.getMessage());
		Assert.assertNotNull("Can't be delete version response null.", deleteResponse);


		ServiceResponse<PackageVersion> generateResponce = OPFEngine.RegistryService.generateWar(aPackage.getId());
		logger.info(generateResponce.getMessage());
		Assert.assertNotNull("Can't be generate response null.", generateResponce);
		Assert.assertNotNull("Can't be item response null.", generateResponce.getItem());
		Assert.assertNotNull("Can't be item files response null.", generateResponce.getItem().getFileVOs());
		for (PackageVersionFile file : generateResponce.getItem().getFileVOs()) {
			InputStream stream = OPFEngine.RegistryService.getPackageArchive(aPackage.getId(), file.getFilename());
			try {
				if (file.getType().equals(PackageFileType.APP_WAR)) {
					ServiceResponse uploadResponse = OPFEngine.RegistryService.uploadPackageVersionXML(aPackage.getId(), file.getType().getExtension(), stream);
					logger.info(uploadResponse.getMessage());
					Assert.assertNotNull("Can't be upload package response null.", uploadResponse);
					Assert.assertNotNull("Can't be upload package item response null.", uploadResponse.getItem());
					continue;
				}

				String s = IOUtils.toString(stream);
				Assert.assertEquals("Can't be stream " + file.getFilename() + " response null.", false, s.isEmpty());
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(stream);
			}
		}

		serviceResponse = OPFEngine.RegistryService.deletePackage(aPackage.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}

	@Test
	public void crudRelationship() {
		Relationship relationship = new Relationship();
		relationship.setName("testShip");
		relationship.setParentId(map.get(Elements.ENTITY).getId());
		relationship.setRelationshipType(RelationshipType.TREE);

		Entity targetEntity = new Entity();
		targetEntity.setId(map.get(Elements.ENTITY).getId());
		relationship.setSourceEntity(targetEntity);
		relationship.setTargetEntity(targetEntity);

		ServiceResponse<Relationship> readAllResponse = OPFEngine.RegistryService.readAllRelationships();

		logger.info(readAllResponse.getMessage());
		Assert.assertNotNull("Can't be null.", readAllResponse.getData());
		Assert.assertEquals("Can't be empty.", false, readAllResponse.getData().isEmpty());

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createRelationship(relationship);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create response null.", response);
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<Relationship> serviceResponse = OPFEngine.RegistryService.readRelationship(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		relationship = serviceResponse.getItem();
		relationship.setName("testShip2");

		response = OPFEngine.RegistryService.updateRelationship(relationship.getId(), relationship);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());

		serviceResponse = OPFEngine.RegistryService.deleteRelationship(relationship.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}

	@Test
	public void crudRootDomain() {
		RootDomain rootDomain = new RootDomain();
		rootDomain.setName("test.ru");

		ServiceResponse<RootDomain> readAllResponse = OPFEngine.RegistryService.readAllRootDomains();

		logger.info(readAllResponse.getMessage());
		Assert.assertNotNull("Can't be null.", readAllResponse.getData());
		Assert.assertEquals("Can't be empty.", false, readAllResponse.getData().isEmpty());

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createRootDomain(rootDomain);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create response null.", response);
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<RootDomain> serviceResponse = OPFEngine.RegistryService.readRootDomain(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		rootDomain = serviceResponse.getItem();
		rootDomain.setName("root.ru");

		response = OPFEngine.RegistryService.updateRootDomain(rootDomain.getId(), rootDomain);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());

		serviceResponse = OPFEngine.RegistryService.deleteRootDomain(rootDomain.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}

	@Test
	public void crudDataBase() {
		Database database = new Database();
		database.setParentId(map.get(Elements.SYSTEM).getId());
		database.setName("testdatabase");
		database.setServerName("localhost");
		database.setProtocol(DatabaseProtocol.JDBC);
		database.setRdbms(DatabaseName.MySQL);
		database.setStatus(RegistryNodeStatus.UNKNOWN);
		database.setUrlPath("opf");
		database.setUsername("root");
		database.setPassword("root");
		database.setPort(3306);

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createDatabase(database);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create response null.", response);
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<Database> serviceResponse = OPFEngine.RegistryService.readDatabase(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		database = serviceResponse.getItem();
		database.setName("testdatabase2");

		response = OPFEngine.RegistryService.updateDatabase(database.getId(), database);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());

		ServiceResponse<Database> readAllResponse = OPFEngine.RegistryService.readDatabasesBySystem(map.get(Elements.PACKAGE).getId());

		logger.info(readAllResponse.getMessage());
		Assert.assertNotNull("Can't be null.", readAllResponse.getData());
		Assert.assertEquals("Can't be empty.", false, readAllResponse.getData().isEmpty());

		serviceResponse = OPFEngine.RegistryService.deleteDatabase(database.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}

	@Test
	public void crudFileStore() {
		Filestore filestore = new Filestore();
		filestore.setParentId(map.get(Elements.SYSTEM).getId());
		filestore.setName("testfilestore");
		filestore.setServerName("localhost");
		filestore.setServerDirectory("localhost");
		filestore.setUrlPath("/temp");
		filestore.setPort(8080);
		filestore.setStatus(RegistryNodeStatus.UNKNOWN);

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createFilestore(filestore);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create response null.", response);
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<Filestore> serviceResponse = OPFEngine.RegistryService.readFilestore(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		filestore = serviceResponse.getItem();
		filestore.setName("testfilestore2");

		response = OPFEngine.RegistryService.updateFilestore(filestore.getId(), filestore);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());


		serviceResponse = OPFEngine.RegistryService.deleteFilestore(filestore.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}

	@Test
	public void crudServer() {
		Server server = new Server();
		server.setParentId(map.get(Elements.SYSTEM).getId());
		server.setName("testServer");
		server.setServerName("localhost");
		server.setProtocol(EntityProtocol.HTTP);
		server.setPort(8080);
		server.setStatus(RegistryNodeStatus.UNKNOWN);

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createServer(server);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create response null.", response);
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<Server> serviceResponse = OPFEngine.RegistryService.readServer(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		server = serviceResponse.getItem();
		server.setName("testServer2");

		response = OPFEngine.RegistryService.updateServer(server.getId(), server);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());


		serviceResponse = OPFEngine.RegistryService.deleteServer(server.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}

	@Test
	public void crudSystem() {
		System system = new System();
		system.setParentId(map.get(Elements.ROOT_DOMAIN).getId());
		system.setName("testSys");
		system.setServerName("localhost");
		system.setStatus(RegistryNodeStatus.UNKNOWN);
		system.setPort(8080);

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createSystem(system);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create response null.", response);
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<System> serviceResponse = OPFEngine.RegistryService.readSystem(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		system = serviceResponse.getItem();
		system.setName("testSys2");

		response = OPFEngine.RegistryService.updateSystem(system.getId(), system);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());

		InputStream stream = OPFEngine.RegistryService.exportXml(map.get(Elements.ROOT_DOMAIN).getId(), "env.xml");
		Assert.assertNotNull("Can't be export xml response null.", stream);

		serviceResponse = OPFEngine.RegistryService.deleteSystem(system.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);

		ServiceResponse importResponse = OPFEngine.RegistryService.importXml(map.get(Elements.ROOT_DOMAIN).getId(), stream);
		logger.info(importResponse.getMessage());
		Assert.assertNotNull("Can't be import response null.", importResponse);

		ServiceResponse<System> readAllResponse = OPFEngine.RegistryService.readAllSystems(true);

		logger.info(readAllResponse.getMessage());
		Assert.assertNotNull("Can't be null.", readAllResponse.getData());
		Assert.assertEquals("Can't be empty.", false, readAllResponse.getData().isEmpty());

		for (System sys : readAllResponse.getData()) {
			if (sys.getName().equals("testSys2")) {
				serviceResponse = OPFEngine.RegistryService.deleteSystem(sys.getId());
				logger.info(serviceResponse.getMessage());
				Assert.assertNotNull("Can't be delete imported response null.", serviceResponse);
			}
		}
	}
}
