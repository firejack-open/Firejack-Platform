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

package net.firejack.platform.api.content;

import net.firejack.platform.api.Elements;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.api.content.domain.*;
import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.api.registry.domain.Domain;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.listener.DomainExecutionListener;
import net.firejack.platform.api.registry.listener.EntityExecutionListener;
import net.firejack.platform.api.registry.listener.PackageExecutionListener;
import net.firejack.platform.api.registry.listener.RootDomainExecutionListener;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ArchiveUtils;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.Paging;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class,
        RootDomainExecutionListener.class,
        PackageExecutionListener.class,
        DomainExecutionListener.class,
        EntityExecutionListener.class
})
public class ContentServiceTest {

    private static final Logger logger = Logger.getLogger(ContentServiceTest.class);

    @javax.annotation.Resource(name = "testContextAttributes")
    private Map<Elements, AbstractDTO> testContextAttributes;

    private Long time;
    private Domain domain;

    @Before
    public void setUp() {
        time = new Date().getTime();
        domain = (Domain) testContextAttributes.get(Elements.DOMAIN);
    }

    @After
    public void tearDown() {

    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    FOLDER TESTS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void folderTest() {
        Folder folder = new Folder();
        folder.setName(time + "-folder");
        folder.setParentId(domain.getId());
        folder.setType("FOLDER");

        ServiceResponse<RegistryNodeTree> response = OPFEngine.ContentService.createFolder(folder);
        logger.info(response.getMessage());
        Assert.assertTrue("Folder should be created.", response.isSuccess());
        RegistryNodeTree registryNodeTree = response.getItem();
        Assert.assertNotNull("Should not be null.", registryNodeTree);
        folder.setId(registryNodeTree.getId());
        folder.setLookup(registryNodeTree.getLookup());

        ServiceResponse<Folder> response2 = OPFEngine.ContentService.readFolder(folder.getId());
        Assert.assertTrue("Folder should be retrieved.", response.isSuccess());
        Folder folder2 = response2.getItem();
        Assert.assertNotNull("Should not be null.", folder2);
        Assert.assertEquals("Folder should be equal.", folder, folder2);

        ServiceResponse<Folder> response3 = OPFEngine.ContentService.readFoldersByParentId(folder.getParentId());
        Assert.assertTrue("Folders should be retrieved.", response3.isSuccess());
        List<Folder> folders = response3.getData();
        Assert.assertNotNull("Should not be null.", folders);
        Assert.assertTrue("Should not be empty.", !folders.isEmpty());

        ServiceResponse<Folder> response4 = OPFEngine.ContentService.readFoldersByLikeLookup(OpenFlame.ROOT_DOMAIN);
        Assert.assertTrue("Folders should be retrieved.", response.isSuccess());
        List<Folder> folders2 = response4.getData();
        Assert.assertNotNull("Should not be null.", folders2);
        Assert.assertTrue("Should not be empty.", !folders2.isEmpty());

        folder.setName(time + "-folder-updated");
        ServiceResponse<RegistryNodeTree> response5 = OPFEngine.ContentService.updateFolder(folder);
        Assert.assertTrue("Folder should be updated.", response5.isSuccess());
        List<RegistryNodeTree> registryNodeTrees = response5.getData();
        Assert.assertNotNull("Should not be null.", registryNodeTrees);
        Assert.assertEquals("Should not be empty.", 1, registryNodeTrees.size());
        RegistryNodeTree updatedFolder = registryNodeTrees.get(0);
        Assert.assertEquals("Should be the same name.", updatedFolder.getName(), folder.getName());

        ServiceResponse response6 = OPFEngine.ContentService.deleteFolder(folder.getId());
        Assert.assertTrue("Folder should be deleted.", response6.isSuccess());
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    COLLECTION TESTS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void collectionTest() {
        Folder folder = createFolder(domain.getId());

        Collection collection = new Collection();
        collection.setName(time + "-collection");
        collection.setParentId(folder.getId());
        collection.setType("COLLECTION");
        collection.setDescription("Some Description");

        ServiceResponse<RegistryNodeTree> response2 = OPFEngine.ContentService.createCollection(collection);
        logger.info(response2.getMessage());
        Assert.assertTrue("Collection should be created.", response2.isSuccess());
        RegistryNodeTree registryNodeTree2 = response2.getItem();
        Assert.assertNotNull("Should not be null.", registryNodeTree2);
        Assert.assertEquals("Should be the same name.", registryNodeTree2.getName(), collection.getName());
        collection.setId(registryNodeTree2.getId());

        ServiceResponse<Collection> response3 = OPFEngine.ContentService.readCollectionsByParentId(folder.getId());
        logger.info(response3.getMessage());
        Assert.assertTrue("Collection should be retrieved.", response3.isSuccess());
        assertExistElement(response3.getData(), collection);

        ServiceResponse<Collection> response4 = OPFEngine.ContentService.readCollection(collection.getId());
        logger.info(response4.getMessage());
        Assert.assertTrue("Collection should be retrieved.", response4.isSuccess());
        Collection collection2 = response4.getItem();
        Assert.assertNotNull("Should not be null.", collection2);
        Assert.assertEquals("Should be the same.", collection, collection2);

        ServiceResponse<Collection> response5 = OPFEngine.ContentService.readCollectionsByLikeLookup(folder.getLookup());
        logger.info(response5.getMessage());
        Assert.assertTrue("Collections should be retrieved.", response5.isSuccess());
        Collection collection3 = response5.getItem();
        Assert.assertNotNull("Should not be null.", collection3);
        Assert.assertEquals("Should be the same.", collection2, collection3);

        collection2.setName(time + "-collection-updated");
        collection2.setMemberships(new ArrayList<Membership>());
        ServiceResponse<RegistryNodeTree> response6 = OPFEngine.ContentService.updateCollection(collection2);
        logger.info(response6.getMessage());
        Assert.assertTrue("Folder should be created.", response6.isSuccess());
        RegistryNodeTree registryNodeTree3 = response6.getItem();
        Assert.assertEquals("Should be the same name.", registryNodeTree3.getName(), collection2.getName());

        ServiceResponse<Collection> response7 = OPFEngine.ContentService.readCollectionsByParentId(folder.getId());
        logger.info(response7.getMessage());
        Assert.assertTrue("Collection should be retrieved.", response7.isSuccess());
        List<Collection> collections = response7.getData();
        Assert.assertNotNull("Should not be null.", collections);
        assertExistElement(collections, collection2);

        ServiceResponse<Collection> response8 = OPFEngine.ContentService.deleteCollection(collection2.getId());
        logger.info(response8.getMessage());
        Assert.assertTrue("Collection should be deleted.", response8.isSuccess());

        deleteFolder(folder);
    }

    @Test
    public void exportImportCollectionTest() {
        Folder folder = createFolder(domain.getId());

        Collection collection = new Collection();
        collection.setName(time + "-collection");
        collection.setParentId(folder.getId());
        collection.setType("COLLECTION");
        collection.setDescription("Some Description");

        ServiceResponse<RegistryNodeTree> response2 = OPFEngine.ContentService.createCollection(collection);
        Assert.assertTrue("Collection should be created.", response2.isSuccess());

        RegistryNodeTree registryNodeTree2 = response2.getItem();
        Assert.assertNotNull("Should not be null.", registryNodeTree2);
        collection.setId(registryNodeTree2.getId());

        ServiceResponse<FileInfo> response3 = OPFEngine.ContentService.exportCollectionArchiveFile(collection.getId());
        FileInfo fileInfo = response3.getItem();
        Assert.assertNotNull("Should not be null.", fileInfo);

        File contentArchive;
        try {
            File tempDir = FileUtils.getTempDirectory();
            contentArchive = FileUtils.create(tempDir, "content.zip");
	        InputStream stream = fileInfo.getStream();
	        FileUtils.writeToFile(contentArchive, stream);
	        IOUtils.closeQuietly(stream);

            ArchiveUtils.unZIP(contentArchive, tempDir);

            File contentXml = FileUtils.create(tempDir, "content.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document document = factory.newDocumentBuilder().parse(contentXml);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
	        Object evaluate = xpath.evaluate("/content/collection", document, XPathConstants.NODE);
	        String collectionName = (String) xpath.evaluate("@name", evaluate, XPathConstants.STRING);
	        Assert.assertEquals("Exported collection should be equal name.", collection.getName(), collectionName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ServiceResponse response4 = OPFEngine.ContentService.deleteCollection(collection.getId());
        Assert.assertTrue("Collection should be deleted.", response4.isSuccess());

        try {
            InputStream contentStream = FileUtils.openInputStream(contentArchive);
            ServiceResponse<FileInfo> response5 = OPFEngine.ContentService.importCollectionArchiveFile(contentStream);
            Assert.assertTrue("Import should be completed.", response5.isSuccess());
	        IOUtils.closeQuietly(contentStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ServiceResponse<Collection> response6 = OPFEngine.ContentService.readCollectionsByLikeLookup(folder.getLookup());
        Assert.assertTrue("Collections should be retrieved.", response6.isSuccess());

        Collection collection2 = response6.getItem();
        Assert.assertNotNull("Should not be null.", collection2);
        Assert.assertEquals("Should be the same name.", collection.getName(), collection2.getName());

        deleteFolder(folder);
    }

    @Test
    public void swapCollectionMembershipsTest() {
        Folder folder = createFolder(domain.getId());
        Collection collection = createCollection(folder);

        TextResource textResource = new TextResource();
        textResource.setName(time + "-text-resource");
        textResource.setParentId(folder.getId());
        textResource.setType("TEXT_RESOURCE");

        TextResourceVersion textResourceVersion = new TextResourceVersion();
        textResourceVersion.setText("text content");
        textResourceVersion.setCulture(Cultures.AMERICAN);
        textResource.setResourceVersion(textResourceVersion);

        ServiceResponse<TextResource> response = OPFEngine.ContentService.createTextResource(textResource);
        logger.info(response.getMessage());
        Assert.assertTrue("TextResource should be created.", response.isSuccess());
        textResource = response.getItem();


        HtmlResource htmlResource = new HtmlResource();
        htmlResource.setName(time + "-html-resource");
        htmlResource.setParentId(folder.getId());
        htmlResource.setType("HTML_RESOURCE");

        HtmlResourceVersion htmlResourceVersion = new HtmlResourceVersion();
        htmlResourceVersion.setHtml("html content");
        htmlResourceVersion.setCulture(Cultures.AMERICAN);
        htmlResource.setResourceVersion(htmlResourceVersion);

        ServiceResponse<HtmlResource> response2 = OPFEngine.ContentService.createHtmlResource(htmlResource);
        logger.info(response2.getMessage());
        Assert.assertTrue("HtmlResource should be created.", response2.isSuccess());
        htmlResource = response2.getItem();

        collection.setName(time + "-collection-updated");

        List<Membership> memberships = new ArrayList<Membership>();
        Membership textResourceMembership = new Membership();
        textResourceMembership.setId(textResource.getId());
        memberships.add(textResourceMembership);

        Membership htmlResourceMembership = new Membership();
        htmlResourceMembership.setId(htmlResource.getId());
        memberships.add(htmlResourceMembership);

        collection.setMemberships(memberships);
        ServiceResponse<RegistryNodeTree> response3 = OPFEngine.ContentService.updateCollection(collection);
        logger.info(response3.getMessage());
        Assert.assertTrue("Folder should be created.", response3.isSuccess());
        RegistryNodeTree registryNodeTree3 = response3.getItem();
        Assert.assertEquals("Should be the same name.", registryNodeTree3.getName(), collection.getName());

        ServiceResponse<Collection> response4 = OPFEngine.ContentService.readCollection(collection.getId());
        logger.info(response4.getMessage());
        Assert.assertTrue("Collection should be retrieved.", response4.isSuccess());
        Collection collection2 = response4.getItem();
        Assert.assertNotNull("Should not be null.", collection2);
        Assert.assertEquals("Should be the same.", collection, collection2);
        memberships = collection2.getMemberships();
        Assert.assertEquals("Collection membership should have two members", 2, memberships.size());

        Assert.assertEquals("The first membership should be TextResource", "TEXT_RESOURCE", memberships.get(0).getType());
        Assert.assertEquals("The second membership should be HtmlResource", "HTML_RESOURCE", memberships.get(1).getType());

        ServiceResponse response5 = OPFEngine.ContentService.swapCollectionMemberships(collection.getId(), memberships.get(0).getId(), memberships.get(1).getId());
        logger.info(response5.getMessage());
        Assert.assertTrue("Collection should be retrieved.", response5.isSuccess());

        ServiceResponse<Collection> response6 = OPFEngine.ContentService.readCollection(collection.getId());
        logger.info(response6.getMessage());
        Assert.assertTrue("Collection should be retrieved.", response6.isSuccess());
        Collection collection3 = response6.getItem();
        Assert.assertNotNull("Should not be null.", collection3);
        Assert.assertEquals("Should be the same.", collection, collection3);
        memberships = collection3.getMemberships();
        Assert.assertEquals("Collection membership should have two members", 2, memberships.size());

        Assert.assertEquals("The first membership should be HtmlResource", "HTML_RESOURCE", memberships.get(0).getType());
        Assert.assertEquals("The second membership should be TextResource", "TEXT_RESOURCE", memberships.get(1).getType());

        deleteCollection(collection);
        deleteFolder(folder);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    RESOURCE TESTS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void readAllCultures() {
        ServiceResponse<Culture> response = OPFEngine.ContentService.readAllCultures();
        logger.info(response.getMessage());
        Assert.assertTrue("Collections should be retrieved.", response.isSuccess());

        List<Culture> cultureList = response.getData();
        List<Cultures> cultures = new ArrayList<Cultures>();
        for (Culture culture : cultureList) {
            cultures.add(culture.getCulture());
        }
        Assert.assertArrayEquals("Cultures should be equals.", Cultures.values(), cultures.toArray());
    }

    @Test
    public void readAvailableCultures() {
        ServiceResponse<Folder> response = OPFEngine.ContentService.readFoldersByParentId(domain.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("Folder should be retrieved folders", response.isSuccess());
        Folder folder = response.getItem();
        Assert.assertEquals("Domain should have only one 'opf-doc' folder.", "opf-doc", folder.getName());

        TextResource textResource = new TextResource();
        textResource.setName(time + "-text-resource-us");
        textResource.setParentId(folder.getId());
        textResource.setType("TEXT_RESOURCE");

        TextResourceVersion textResourceVersion = new TextResourceVersion();
        textResourceVersion.setText("text content");
        textResourceVersion.setCulture(Cultures.AMERICAN);
        textResource.setResourceVersion(textResourceVersion);

        ServiceResponse<TextResource> response2 = OPFEngine.ContentService.createTextResource(textResource);
        logger.info(response2.getMessage());
        Assert.assertTrue("TextResource should be created.", response2.isSuccess());
        textResource = response2.getItem();

        TextResource textResource2 = new TextResource();
        textResource2.setName(time + "-text-resource-gb");
        textResource2.setParentId(folder.getId());
        textResource2.setType("TEXT_RESOURCE");

        TextResourceVersion textResourceVersion2 = new TextResourceVersion();
        textResourceVersion2.setText("text content");
        textResourceVersion2.setCulture(Cultures.BRITISH);
        textResource2.setResourceVersion(textResourceVersion2);

        ServiceResponse<TextResource> response3 = OPFEngine.ContentService.createTextResource(textResource2);
        logger.info(response3.getMessage());
        Assert.assertTrue("TextResource should be created.", response3.isSuccess());
        textResource2 = response3.getItem();

        ServiceResponse<Collection> response4 = OPFEngine.ContentService.readCollectionsByParentId(folder.getId());
        logger.info(response4.getMessage());
        Assert.assertTrue("Collection should be found.", response4.isSuccess());
        Collection collection = response4.getItem();

        List<Membership> memberships = new ArrayList<Membership>();
        Membership membership = new Membership();
        membership.setId(textResource.getId());
        memberships.add(membership);

        Membership membership2 = new Membership();
        membership2.setId(textResource2.getId());
        memberships.add(membership2);

        collection.setMemberships(memberships);
        ServiceResponse<RegistryNodeTree> response5 = OPFEngine.ContentService.updateCollection(collection);
        logger.info(response5.getMessage());
        Assert.assertTrue("Folder should be updated.", response5.isSuccess());
        RegistryNodeTree registryNodeTree = response5.getItem();
        Assert.assertEquals("Should be the same name.", registryNodeTree.getName(), collection.getName());

        ServiceResponse<Culture> response8 = OPFEngine.ContentService.readAvailableCultures(domain.getId());
        logger.info(response8.getMessage());
        Assert.assertTrue("Cultures should be retrieved.", response8.isSuccess());
        Assert.assertArrayEquals("Should be available AMERICAN and BRITISH cultures",
                new Cultures[] { Cultures.AMERICAN, Cultures.BRITISH },
                new Cultures[] {response8.getData().get(0).getCulture(), response8.getData().get(1).getCulture() });

        deleteFolder(folder);
    }

    @Test
    public void readResourcesByParentIdTest() {
        ServiceResponse<Folder> response = OPFEngine.ContentService.readFoldersByParentId(domain.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("Folder should be retrieved folders", response.isSuccess());
        Folder folder = response.getItem();
        Assert.assertEquals("Domain should have only one 'opf-doc' folder.", "opf-doc", folder.getName());

        TextResource textResource = new TextResource();
        textResource.setName(time + "-text-resource-us");
        textResource.setParentId(folder.getId());
        textResource.setType("TEXT_RESOURCE");

        TextResourceVersion textResourceVersion = new TextResourceVersion();
        textResourceVersion.setText("text content");
        textResourceVersion.setCulture(Cultures.AMERICAN);
        textResource.setResourceVersion(textResourceVersion);

        ServiceResponse<TextResource> response2 = OPFEngine.ContentService.createTextResource(textResource);
        logger.info(response2.getMessage());
        Assert.assertTrue("TextResource should be created.", response2.isSuccess());
        textResource = response2.getItem();

        TextResource textResource2 = new TextResource();
        textResource2.setName(time + "-text-resource-gb");
        textResource2.setParentId(folder.getId());
        textResource2.setType("TEXT_RESOURCE");

        TextResourceVersion textResourceVersion2 = new TextResourceVersion();
        textResourceVersion2.setText("text content");
        textResourceVersion2.setCulture(Cultures.BRITISH);
        textResource2.setResourceVersion(textResourceVersion2);

        ServiceResponse<TextResource> response3 = OPFEngine.ContentService.createTextResource(textResource2);
        logger.info(response3.getMessage());
        Assert.assertTrue("TextResource should be created.", response3.isSuccess());
        textResource2 = response3.getItem();

        TextResource textResource3 = new TextResource();
        textResource3.setName(time + "-text-resource-ru");
        textResource3.setParentId(folder.getId());
        textResource3.setType("TEXT_RESOURCE");

        TextResourceVersion textResourceVersion3 = new TextResourceVersion();
        textResourceVersion3.setText("text content");
        textResourceVersion3.setCulture(Cultures.RUSSIAN);
        textResource3.setResourceVersion(textResourceVersion3);

        ServiceResponse<TextResource> response4 = OPFEngine.ContentService.createTextResource(textResource3);
        logger.info(response4.getMessage());
        Assert.assertTrue("TextResource should be created.", response4.isSuccess());
        textResource3 = response4.getItem();

        ServiceResponse<Resource> response5 = OPFEngine.ContentService.readResourcesByParentId(folder.getId(), null, new Paging());
        logger.info(response5.getMessage());
        Assert.assertTrue("Service should be return resources.", response3.isSuccess());
        List<Resource> resources = response5.getData();
        Assert.assertEquals("Service should find three resources.", new Integer(3), new Integer(resources.size()));
        assertExistElement(resources, textResource);
        assertExistElement(resources, textResource2);
        assertExistElement(resources, textResource3);

        List<Long> exceptIds = new ArrayList<Long>();
        exceptIds.add(textResource.getId());
        ServiceResponse<Resource> response6 = OPFEngine.ContentService.readResourcesByParentId(folder.getId(), exceptIds, new Paging());
        logger.info(response6.getMessage());
        Assert.assertTrue("Service should be return resources.", response3.isSuccess());
        resources = response6.getData();
        Assert.assertEquals("Service should find two resources.", new Integer(2), new Integer(resources.size()));
        assertExistElement(resources, textResource2);
        assertExistElement(resources, textResource3);

        ServiceResponse<Resource> response7 = OPFEngine.ContentService.readResourcesByParentId(folder.getId(), null, new Paging(1, 1));
        logger.info(response7.getMessage());
        Assert.assertTrue("Service should be return resources.", response3.isSuccess());
        resources = response7.getData();
        Assert.assertEquals("Service should find one resource.", new Integer(1), new Integer(resources.size()));
        assertExistElement(resources, textResource2);

        deleteFolder(folder);
    }

    @Test
    public void searchResourcesByParentIdAndTermTest() {
        ServiceResponse<Folder> response = OPFEngine.ContentService.readFoldersByParentId(domain.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("Folder should be retrieved folders", response.isSuccess());
        Folder folder = response.getItem();
        Assert.assertEquals("Domain should have only one 'opf-doc' folder.", "opf-doc", folder.getName());
        Collection collection = createCollection(folder);

        TextResource textResource = new TextResource();
        textResource.setName(time + "->text-resource-us");
        textResource.setParentId(folder.getId());
        textResource.setType("TEXT_RESOURCE");

        TextResourceVersion textResourceVersion = new TextResourceVersion();
        textResourceVersion.setText("text content");
        textResourceVersion.setCulture(Cultures.AMERICAN);
        textResource.setResourceVersion(textResourceVersion);

        ServiceResponse<TextResource> response2 = OPFEngine.ContentService.createTextResource(textResource);
        logger.info(response2.getMessage());
        Assert.assertTrue("TextResource should be created.", response2.isSuccess());
        textResource = response2.getItem();

        List<Membership> memberships = new ArrayList<Membership>();
        Membership textResourceMembership = new Membership();
        textResourceMembership.setId(textResource.getId());
        memberships.add(textResourceMembership);

        collection.setMemberships(memberships);
        ServiceResponse<RegistryNodeTree> response3 = OPFEngine.ContentService.updateCollection(collection);
        logger.info(response3.getMessage());

        ServiceResponse<Resource> response4 = OPFEngine.ContentService.searchResourcesByParentIdAndTerm(domain.getId(), "text-resource-us");
        logger.info(response4.getMessage());
        List<Resource> resources = response4.getData();
        Assert.assertTrue("->text-resource-us", response4.isSuccess());
        Assert.assertEquals("Should be equal 1", 1, resources.size());

        assertExistElement(resources, textResource);

        deleteCollection(collection);
    }

    @Test
    public void uploadResourceFile() {
        InputStream inputStream = this.getClass().getResourceAsStream("/files/Chrysanthemum.jpg");
        ServiceResponse<FileInfo> response = OPFEngine.ContentService.uploadResourceFile(ResourceType.IMAGE.name(), null, inputStream, "Chrysanthemum.jpg");
        logger.info(response.getMessage());
        Assert.assertTrue("Image Resource File should be uploaded.", response.isSuccess());
        ImageFileInfo imageFileInfo = (ImageFileInfo) response.getItem();
        Assert.assertNotNull("Image Resource File should have temporary file name", imageFileInfo.getFilename());
        Assert.assertEquals("Image Resource File should have original file name", "Chrysanthemum.jpg", imageFileInfo.getOrgFilename());
        Assert.assertEquals("Image Resource File should have width", new Integer(1024), imageFileInfo.getWidth());
        Assert.assertEquals("Image Resource File should have height", new Integer(768), imageFileInfo.getHeight());

        ServiceResponse<FileInfo> response2 = OPFEngine.ContentService.getTemporaryUploadedFile(imageFileInfo.getFilename());
        logger.info(response.getMessage());
        Assert.assertTrue("Image Resource File should be downloaded.", response2.isSuccess());
        IOUtils.closeQuietly(inputStream);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    TEXT RESOURCE TESTS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void textResourceTest() {
        Folder folder = createFolder(domain.getId());

        TextResource textResource = new TextResource();
        textResource.setName(time + "-text-resource");
        textResource.setParentId(folder.getId());
        textResource.setType("TEXT_RESOURCE");

        TextResourceVersion textResourceVersion = new TextResourceVersion();
        textResourceVersion.setText("text content");
        textResourceVersion.setCulture(Cultures.AMERICAN);
        textResource.setResourceVersion(textResourceVersion);

        ServiceResponse<TextResource> response = OPFEngine.ContentService.createTextResource(textResource);
        logger.info(response.getMessage());
        Assert.assertTrue("TextResource should be created.", response.isSuccess());
        textResource = response.getItem();

        ServiceResponse<TextResource> response2 = OPFEngine.ContentService.readTextResourceById(textResource.getId());
        logger.info(response2.getMessage());
        Assert.assertTrue("TextResource should be retrieved.", response2.isSuccess());
        TextResource textResource2 = response2.getItem();
        Assert.assertEquals("TextResource should be the same.", textResource, textResource2);

        ServiceResponse<TextResource> response21 = OPFEngine.ContentService.readTextResourceByLookup(textResource.getLookup());
        logger.info(response21.getMessage());
        Assert.assertTrue("TextResource should be retrieved.", response21.isSuccess());
        TextResource textResource21 = response21.getItem();
        Assert.assertEquals("TextResource should be the same.", textResource, textResource21);

        ServiceResponse<TextResource> response3 = OPFEngine.ContentService.createNewTextResourceVersions(textResource.getId());
        logger.info(response3.getMessage());
        Assert.assertTrue("Service should create new text resource versions from the last version.", response2.isSuccess());
        TextResource textResource3 = response3.getItem();
        Assert.assertEquals("Version of new text resource versions should be greater then previous.",
                new Integer(textResource.getLastVersion() + 1), textResource3.getLastVersion());

        ServiceResponse<TextResource> response4 = OPFEngine.ContentService.createNewTextResourceVersions(textResource.getId(), 1);
        logger.info(response4.getMessage());
        Assert.assertTrue("Service should create new text resource versions from the last version.", response2.isSuccess());
        TextResource textResource4 = response4.getItem();
        Assert.assertEquals("Version of new text resource versions should be greater then previous.",
                new Integer(textResource3.getLastVersion() + 1), textResource4.getLastVersion());

        textResource.setName(time + "-text-resource-updated");
        ServiceResponse<TextResource> response5 = OPFEngine.ContentService.updateTextResource(textResource);
        logger.info(response5.getMessage());
        Assert.assertTrue("Service should update new text resource.", response5.isSuccess());
        TextResource textResource5 = response5.getItem();
        Assert.assertEquals("Name should be updated",textResource.getName(), textResource5.getName());

        textResourceVersion = new TextResourceVersion();
        textResourceVersion.setText("text content");
        textResourceVersion.setCulture(Cultures.RUSSIAN);
        textResourceVersion.setVersion(2);
        textResource5.setResourceVersion(textResourceVersion);
        textResource5.setSelectedVersion(2);
        ServiceResponse response6 = OPFEngine.ContentService.createTextResource(textResource5);
        logger.info(response6.getMessage());
        Assert.assertTrue("TextResource should be updated.", response6.isSuccess());

        ServiceResponse<TextResource> response7 = OPFEngine.ContentService.readTextResourceByIdCultureAndVersion(textResource.getId(), "RUSSIAN", 2);
        logger.info(response7.getMessage());
        Assert.assertTrue("TextResource should be retrieved.", response7.isSuccess());
        TextResource textResource7 = response7.getItem();
        TextResourceVersion textResourceVersion7 = textResource7.getResourceVersion();
        Assert.assertEquals("TextResourceVersion should have Russian culture.", Cultures.RUSSIAN, textResourceVersion7.getCulture());
        Assert.assertEquals("TextResourceVersion should be second version", new Integer(2), textResourceVersion7.getVersion());

        ServiceResponse response8 = OPFEngine.ContentService.deleteTextResourceVersion(textResource.getId(), "RUSSIAN", 2);
        logger.info(response8.getMessage());
        Assert.assertTrue("TextResource should be deleted.", response8.isSuccess());

        ServiceResponse<TextResource> response9 = OPFEngine.ContentService.readTextResourceByIdCultureAndVersion(textResource.getId(), "RUSSIAN", 2);
        logger.info(response9.getMessage());
        Assert.assertFalse("TextResource should not be retrieved.", response9.isSuccess());

        ServiceResponse response10 = OPFEngine.ContentService.deleteTextResource(textResource.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("TextResource should be deleted.", response10.isSuccess());

        deleteFolder(folder);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    HTML RESOURCE TESTS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void htmlResourceTest() {
        Folder folder = createFolder(domain.getId());

        HtmlResource htmlResource = new HtmlResource();
        htmlResource.setName(time + "-html-resource");
        htmlResource.setParentId(folder.getId());
        htmlResource.setType("HTML_RESOURCE");

        HtmlResourceVersion htmlResourceVersion = new HtmlResourceVersion();
        htmlResourceVersion.setHtml("html content");
        htmlResourceVersion.setCulture(Cultures.AMERICAN);
        htmlResource.setResourceVersion(htmlResourceVersion);

        ServiceResponse<HtmlResource> response = OPFEngine.ContentService.createHtmlResource(htmlResource);
        logger.info(response.getMessage());
        Assert.assertTrue("HtmlResource should be created.", response.isSuccess());
        htmlResource = response.getItem();

        ServiceResponse<HtmlResource> response2 = OPFEngine.ContentService.readHtmlResourceById(htmlResource.getId());
        logger.info(response2.getMessage());
        Assert.assertTrue("HtmlResource should be retrieved.", response2.isSuccess());
        HtmlResource htmlResource2 = response2.getItem();
        Assert.assertEquals("HtmlResource should be the same.", htmlResource, htmlResource2);

        ServiceResponse<HtmlResource> response21 = OPFEngine.ContentService.readHtmlResourceByLookup(htmlResource.getLookup());
        logger.info(response21.getMessage());
        Assert.assertTrue("HtmlResource should be retrieved.", response21.isSuccess());
        HtmlResource htmlResource21 = response21.getItem();
        Assert.assertEquals("HtmlResource should be the same.", htmlResource, htmlResource21);

        ServiceResponse<HtmlResource> response3 = OPFEngine.ContentService.createNewHtmlResourceVersions(htmlResource.getId());
        logger.info(response3.getMessage());
        Assert.assertTrue("Service should create new html resource versions from the last version.", response2.isSuccess());
        HtmlResource htmlResource3 = response3.getItem();
        Assert.assertEquals("Version of new html resource versions should be greater then previous.",
                new Integer(htmlResource.getLastVersion() + 1), htmlResource3.getLastVersion());

        ServiceResponse<HtmlResource> response4 = OPFEngine.ContentService.createNewHtmlResourceVersions(htmlResource.getId(), 1);
        logger.info(response4.getMessage());
        Assert.assertTrue("Service should create new html resource versions from the last version.", response2.isSuccess());
        HtmlResource htmlResource4 = response4.getItem();
        Assert.assertEquals("Version of new html resource versions should be greater then previous.",
                new Integer(htmlResource3.getLastVersion() + 1), htmlResource4.getLastVersion());

        htmlResource.setName(time + "-html-resource-updated");
        ServiceResponse<HtmlResource> response5 = OPFEngine.ContentService.updateHtmlResource(htmlResource);
        logger.info(response5.getMessage());
        Assert.assertTrue("Service should update new html resource.", response5.isSuccess());
        HtmlResource htmlResource5 = response5.getItem();
        Assert.assertEquals("Name should be updated",htmlResource.getName(), htmlResource5.getName());

        htmlResourceVersion = new HtmlResourceVersion();
        htmlResourceVersion.setHtml("html content");
        htmlResourceVersion.setCulture(Cultures.RUSSIAN);
        htmlResourceVersion.setVersion(2);
        htmlResource5.setResourceVersion(htmlResourceVersion);
        htmlResource5.setSelectedVersion(2);
        ServiceResponse response6 = OPFEngine.ContentService.createHtmlResource(htmlResource5);
        logger.info(response6.getMessage());
        Assert.assertTrue("HtmlResource should be updated.", response6.isSuccess());

        ServiceResponse<HtmlResource> response7 = OPFEngine.ContentService.readHtmlResourceByIdCultureAndVersion(htmlResource.getId(), "RUSSIAN", 2);
        logger.info(response7.getMessage());
        Assert.assertTrue("HtmlResource should be retrieved.", response7.isSuccess());
        HtmlResource htmlResource7 = response7.getItem();
        HtmlResourceVersion htmlResourceVersion7 = htmlResource7.getResourceVersion();
        Assert.assertEquals("HtmlResourceVersion should have Russian culture.", Cultures.RUSSIAN, htmlResourceVersion7.getCulture());
        Assert.assertEquals("HtmlResourceVersion should be second version", new Integer(2), htmlResourceVersion7.getVersion());

        ServiceResponse response8 = OPFEngine.ContentService.deleteHtmlResourceVersion(htmlResource.getId(), "RUSSIAN", 2);
        logger.info(response8.getMessage());
        Assert.assertTrue("HtmlResource should be deleted.", response8.isSuccess());

        ServiceResponse<HtmlResource> response9 = OPFEngine.ContentService.readHtmlResourceByIdCultureAndVersion(htmlResource.getId(), "RUSSIAN", 2);
        logger.info(response9.getMessage());
        Assert.assertFalse("HtmlResource should not be retrieved.", response9.isSuccess());

        ServiceResponse response10 = OPFEngine.ContentService.deleteHtmlResource(htmlResource.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("HtmlResource should be deleted.", response10.isSuccess());

        deleteFolder(folder);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    IMAGE RESOURCE TESTS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void imageResourceTest() {
        Folder folder = createFolder(domain.getId());

        InputStream inputStream = this.getClass().getResourceAsStream("/files/Chrysanthemum.jpg");
        ServiceResponse<FileInfo> response = OPFEngine.ContentService.uploadResourceFile(ResourceType.IMAGE.name(), null, inputStream, "Chrysanthemum.jpg");
        logger.info(response.getMessage());
        Assert.assertTrue("Image Resource File should be uploaded.", response.isSuccess());
        ImageFileInfo imageFileInfo = (ImageFileInfo) response.getItem();
        IOUtils.closeQuietly(inputStream);

        ImageResource imageResource = new ImageResource();
        imageResource.setName(time + "-image-resource");
        imageResource.setParentId(folder.getId());
        imageResource.setType("IMAGE_RESOURCE");

        ImageResourceVersion imageResourceVersion = new ImageResourceVersion();
        imageResourceVersion.setTitle("image title");
        imageResourceVersion.setCulture(Cultures.AMERICAN);
        imageResourceVersion.setHeight(imageFileInfo.getHeight());
        imageResourceVersion.setWidth(imageFileInfo.getWidth());
        imageResourceVersion.setResourceFileTemporaryName(imageFileInfo.getFilename());
        imageResourceVersion.setResourceFileOriginalName(imageFileInfo.getOrgFilename());
        imageResource.setResourceVersion(imageResourceVersion);

        ServiceResponse<ImageResource> response1 = OPFEngine.ContentService.createImageResource(imageResource);
        logger.info(response1.getMessage());
        Assert.assertTrue("ImageResource should be created.", response1.isSuccess());
        imageResource = response1.getItem();

        ServiceResponse<ImageResource> response2 = OPFEngine.ContentService.readImageResourceById(imageResource.getId());
        logger.info(response2.getMessage());
        Assert.assertTrue("ImageResource should be retrieved.", response2.isSuccess());
        ImageResource imageResource2 = response2.getItem();
        Assert.assertEquals("ImageResource should be the same.", imageResource, imageResource2);

        ServiceResponse<ImageResource> response21 = OPFEngine.ContentService.readImageResourceByLookup(imageResource.getLookup());
        logger.info(response21.getMessage());
        Assert.assertTrue("ImageResource should be retrieved.", response21.isSuccess());
        ImageResource imageResource21 = response21.getItem();
        Assert.assertEquals("ImageResource should be the same.", imageResource, imageResource21);

        ServiceResponse<ImageResource> response3 = OPFEngine.ContentService.createNewImageResourceVersions(imageResource.getId());
        logger.info(response3.getMessage());
        Assert.assertTrue("Service should create new image resource versions from the last version.", response2.isSuccess());
        ImageResource imageResource3 = response3.getItem();
        Assert.assertEquals("Version of new image resource versions should be greater then previous.",
                new Integer(imageResource.getLastVersion() + 1), imageResource3.getLastVersion());

        ServiceResponse<ImageResource> response4 = OPFEngine.ContentService.createNewImageResourceVersions(imageResource.getId(), 1);
        logger.info(response4.getMessage());
        Assert.assertTrue("Service should create new image resource versions from the last version.", response2.isSuccess());
        ImageResource imageResource4 = response4.getItem();
        Assert.assertEquals("Version of new image resource versions should be greater then previous.",
                new Integer(imageResource3.getLastVersion() + 1), imageResource4.getLastVersion());

        imageResource.setName(time + "-image-resource-updated");
        ServiceResponse<ImageResource> response5 = OPFEngine.ContentService.updateImageResource(imageResource);
        logger.info(response5.getMessage());
        Assert.assertTrue("Service should update new image resource.", response5.isSuccess());
        ImageResource imageResource5 = response5.getItem();
        Assert.assertEquals("Name should be updated",imageResource.getName(), imageResource5.getName());

        imageResourceVersion = new ImageResourceVersion();
        imageResourceVersion.setTitle("image title");
        imageResourceVersion.setCulture(Cultures.RUSSIAN);
        imageResourceVersion.setHeight(600);
        imageResourceVersion.setWidth(800);
        imageResourceVersion.setVersion(2);
        imageResource5.setResourceVersion(imageResourceVersion);
	    imageResourceVersion.setResourceFileTemporaryName(imageFileInfo.getFilename());
	    imageResourceVersion.setResourceFileOriginalName(imageFileInfo.getOrgFilename());
        imageResource5.setSelectedVersion(2);
        ServiceResponse response6 = OPFEngine.ContentService.createImageResource(imageResource5);
        logger.info(response6.getMessage());
        Assert.assertTrue("ImageResource should be updated.", response6.isSuccess());

        ServiceResponse<ImageResource> response7 = OPFEngine.ContentService.readImageResourceByIdCultureAndVersion(imageResource.getId(), "RUSSIAN", 2);
        logger.info(response7.getMessage());
        Assert.assertTrue("ImageResource should be retrieved.", response7.isSuccess());
        ImageResource imageResource7 = response7.getItem();
        ImageResourceVersion imageResourceVersion7 = imageResource7.getResourceVersion();
        Assert.assertEquals("ImageResourceVersion should have Russian culture.", Cultures.RUSSIAN, imageResourceVersion7.getCulture());
        Assert.assertEquals("ImageResourceVersion should be second version", new Integer(2), imageResourceVersion7.getVersion());

        ServiceResponse response8 = OPFEngine.ContentService.deleteImageResourceVersion(imageResource.getId(), "RUSSIAN", 2);
        logger.info(response8.getMessage());
        Assert.assertTrue("ImageResource should be deleted.", response8.isSuccess());

        ServiceResponse<ImageResource> response9 = OPFEngine.ContentService.readImageResourceByIdCultureAndVersion(imageResource.getId(), "RUSSIAN", 2);
        logger.info(response9.getMessage());
        Assert.assertFalse("ImageResource should not be retrieved.", response9.isSuccess());

        ServiceResponse response10 = OPFEngine.ContentService.deleteImageResource(imageResource.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("ImageResource should be deleted.", response10.isSuccess());

        deleteFolder(folder);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    AUDIO RESOURCE TESTS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void audioResourceTest() {
        Folder folder = createFolder(domain.getId());

        InputStream inputStream = this.getClass().getResourceAsStream("/files/Chrysanthemum.jpg");
        ServiceResponse<FileInfo> response = OPFEngine.ContentService.uploadResourceFile(ResourceType.AUDIO.name(), null, inputStream, "Chrysanthemum.jpg");
        logger.info(response.getMessage());
        Assert.assertTrue("Audio Resource File should be uploaded.", response.isSuccess());
        FileInfo fileInfo = response.getItem();
        IOUtils.closeQuietly(inputStream);

        AudioResource audioResource = new AudioResource();
        audioResource.setName(time + "-audio-resource");
        audioResource.setParentId(folder.getId());
        audioResource.setType("IMAGE_RESOURCE");

        AudioResourceVersion audioResourceVersion = new AudioResourceVersion();
        audioResourceVersion.setCulture(Cultures.AMERICAN);
        audioResourceVersion.setResourceFileTemporaryName(fileInfo.getFilename());
        audioResourceVersion.setResourceFileOriginalName(fileInfo.getOrgFilename());
        audioResource.setResourceVersion(audioResourceVersion);

        ServiceResponse<AudioResource> response1 = OPFEngine.ContentService.createAudioResource(audioResource);
        logger.info(response1.getMessage());
        Assert.assertTrue("AudioResource should be created.", response1.isSuccess());
        audioResource = response1.getItem();

        ServiceResponse<AudioResource> response2 = OPFEngine.ContentService.readAudioResourceById(audioResource.getId());
        logger.info(response2.getMessage());
        Assert.assertTrue("AudioResource should be retrieved.", response2.isSuccess());
        AudioResource audioResource2 = response2.getItem();
        Assert.assertEquals("AudioResource should be the same.", audioResource, audioResource2);

        ServiceResponse<AudioResource> response21 = OPFEngine.ContentService.readAudioResourceByLookup(audioResource.getLookup());
        logger.info(response21.getMessage());
        Assert.assertTrue("AudioResource should be retrieved.", response21.isSuccess());
        AudioResource audioResource21 = response21.getItem();
        Assert.assertEquals("AudioResource should be the same.", audioResource, audioResource21);

        ServiceResponse<AudioResource> response3 = OPFEngine.ContentService.createNewAudioResourceVersions(audioResource.getId());
        logger.info(response3.getMessage());
        Assert.assertTrue("Service should create new audio resource versions from the last version.", response2.isSuccess());
        AudioResource audioResource3 = response3.getItem();
        Assert.assertEquals("Version of new audio resource versions should be greater then previous.",
                new Integer(audioResource.getLastVersion() + 1), audioResource3.getLastVersion());

        ServiceResponse<AudioResource> response4 = OPFEngine.ContentService.createNewAudioResourceVersions(audioResource.getId(), 1);
        logger.info(response4.getMessage());
        Assert.assertTrue("Service should create new audio resource versions from the last version.", response2.isSuccess());
        AudioResource audioResource4 = response4.getItem();
        Assert.assertEquals("Version of new audio resource versions should be greater then previous.",
                new Integer(audioResource3.getLastVersion() + 1), audioResource4.getLastVersion());

        audioResource.setName(time + "-audio-resource-updated");
        ServiceResponse<AudioResource> response5 = OPFEngine.ContentService.updateAudioResource(audioResource);
        logger.info(response5.getMessage());
        Assert.assertTrue("Service should update new audio resource.", response5.isSuccess());
        AudioResource audioResource5 = response5.getItem();
        Assert.assertEquals("Name should be updated",audioResource.getName(), audioResource5.getName());

        audioResourceVersion = new AudioResourceVersion();
        audioResourceVersion.setCulture(Cultures.RUSSIAN);
        audioResourceVersion.setVersion(2);
        audioResource5.setResourceVersion(audioResourceVersion);
	    audioResourceVersion.setResourceFileTemporaryName(fileInfo.getFilename());
	    audioResourceVersion.setResourceFileOriginalName(fileInfo.getOrgFilename());
        audioResource5.setSelectedVersion(2);
        ServiceResponse response6 = OPFEngine.ContentService.createAudioResource(audioResource5);
        logger.info(response6.getMessage());
        Assert.assertTrue("AudioResource should be updated.", response6.isSuccess());

        ServiceResponse<AudioResource> response7 = OPFEngine.ContentService.readAudioResourceByIdCultureAndVersion(audioResource.getId(), "RUSSIAN", 2);
        logger.info(response7.getMessage());
        Assert.assertTrue("AudioResource should be retrieved.", response7.isSuccess());
        AudioResource audioResource7 = response7.getItem();
        AudioResourceVersion audioResourceVersion7 = audioResource7.getResourceVersion();
        Assert.assertEquals("AudioResourceVersion should have Russian culture.", Cultures.RUSSIAN, audioResourceVersion7.getCulture());
        Assert.assertEquals("AudioResourceVersion should be second version", new Integer(2), audioResourceVersion7.getVersion());

        ServiceResponse response8 = OPFEngine.ContentService.deleteAudioResourceVersion(audioResource.getId(), "RUSSIAN", 2);
        logger.info(response8.getMessage());
        Assert.assertTrue("AudioResource should be deleted.", response8.isSuccess());

        ServiceResponse<AudioResource> response9 = OPFEngine.ContentService.readAudioResourceByIdCultureAndVersion(audioResource.getId(), "RUSSIAN", 2);
        logger.info(response9.getMessage());
        Assert.assertFalse("AudioResource should not be retrieved.", response9.isSuccess());

        ServiceResponse response10 = OPFEngine.ContentService.deleteAudioResource(audioResource.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("AudioResource should be deleted.", response10.isSuccess());

        deleteFolder(folder);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    AUDIO RESOURCE TESTS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void videoResourceTest() {
        Folder folder = createFolder(domain.getId());

        InputStream inputStream = this.getClass().getResourceAsStream("/files/Chrysanthemum.jpg");
        ServiceResponse<FileInfo> response = OPFEngine.ContentService.uploadResourceFile(ResourceType.AUDIO.name(), null, inputStream, "Chrysanthemum.jpg");
        logger.info(response.getMessage());
        Assert.assertTrue("Video Resource File should be uploaded.", response.isSuccess());
        FileInfo fileInfo = response.getItem();
        IOUtils.closeQuietly(inputStream);

        VideoResource videoResource = new VideoResource();
        videoResource.setName(time + "-video-resource");
        videoResource.setParentId(folder.getId());
        videoResource.setType("IMAGE_RESOURCE");

        VideoResourceVersion videoResourceVersion = new VideoResourceVersion();
        videoResourceVersion.setCulture(Cultures.AMERICAN);
        videoResourceVersion.setResourceFileTemporaryName(fileInfo.getFilename());
        videoResourceVersion.setResourceFileOriginalName(fileInfo.getOrgFilename());
        videoResource.setResourceVersion(videoResourceVersion);

        ServiceResponse<VideoResource> response1 = OPFEngine.ContentService.createVideoResource(videoResource);
        logger.info(response1.getMessage());
        Assert.assertTrue("VideoResource should be created.", response1.isSuccess());
        videoResource = response1.getItem();

        ServiceResponse<VideoResource> response2 = OPFEngine.ContentService.readVideoResourceById(videoResource.getId());
        logger.info(response2.getMessage());
        Assert.assertTrue("VideoResource should be retrieved.", response2.isSuccess());
        VideoResource videoResource2 = response2.getItem();
        Assert.assertEquals("VideoResource should be the same.", videoResource, videoResource2);

        ServiceResponse<VideoResource> response21 = OPFEngine.ContentService.readVideoResourceByLookup(videoResource.getLookup());
        logger.info(response21.getMessage());
        Assert.assertTrue("VideoResource should be retrieved.", response21.isSuccess());
        VideoResource videoResource21 = response21.getItem();
        Assert.assertEquals("VideoResource should be the same.", videoResource, videoResource21);

        ServiceResponse<VideoResource> response3 = OPFEngine.ContentService.createNewVideoResourceVersions(videoResource.getId());
        logger.info(response3.getMessage());
        Assert.assertTrue("Service should create new video resource versions from the last version.", response2.isSuccess());
        VideoResource videoResource3 = response3.getItem();
        Assert.assertEquals("Version of new video resource versions should be greater then previous.",
                new Integer(videoResource.getLastVersion() + 1), videoResource3.getLastVersion());

        ServiceResponse<VideoResource> response4 = OPFEngine.ContentService.createNewVideoResourceVersions(videoResource.getId(), 1);
        logger.info(response4.getMessage());
        Assert.assertTrue("Service should create new video resource versions from the last version.", response2.isSuccess());
        VideoResource videoResource4 = response4.getItem();
        Assert.assertEquals("Version of new video resource versions should be greater then previous.",
                new Integer(videoResource3.getLastVersion() + 1), videoResource4.getLastVersion());

        videoResource.setName(time + "-video-resource-updated");
        ServiceResponse<VideoResource> response5 = OPFEngine.ContentService.updateVideoResource(videoResource);
        logger.info(response5.getMessage());
        Assert.assertTrue("Service should update new video resource.", response5.isSuccess());
        VideoResource videoResource5 = response5.getItem();
        Assert.assertEquals("Name should be updated",videoResource.getName(), videoResource5.getName());

        videoResourceVersion = new VideoResourceVersion();
        videoResourceVersion.setCulture(Cultures.RUSSIAN);
        videoResourceVersion.setVersion(2);
        videoResource5.setResourceVersion(videoResourceVersion);
	    videoResourceVersion.setResourceFileTemporaryName(fileInfo.getFilename());
	    videoResourceVersion.setResourceFileOriginalName(fileInfo.getOrgFilename());
        videoResource5.setSelectedVersion(2);
        ServiceResponse response6 = OPFEngine.ContentService.createVideoResource(videoResource5);
        logger.info(response6.getMessage());
        Assert.assertTrue("VideoResource should be updated.", response6.isSuccess());

        ServiceResponse<VideoResource> response7 = OPFEngine.ContentService.readVideoResourceByIdCultureAndVersion(videoResource.getId(), "RUSSIAN", 2);
        logger.info(response7.getMessage());
        Assert.assertTrue("VideoResource should be retrieved.", response7.isSuccess());
        VideoResource videoResource7 = response7.getItem();
        VideoResourceVersion videoResourceVersion7 = videoResource7.getResourceVersion();
        Assert.assertEquals("VideoResourceVersion should have Russian culture.", Cultures.RUSSIAN, videoResourceVersion7.getCulture());
        Assert.assertEquals("VideoResourceVersion should be second version", new Integer(2), videoResourceVersion7.getVersion());

        ServiceResponse response8 = OPFEngine.ContentService.deleteVideoResourceVersion(videoResource.getId(), "RUSSIAN", 2);
        logger.info(response8.getMessage());
        Assert.assertTrue("VideoResource should be deleted.", response8.isSuccess());

        ServiceResponse<VideoResource> response9 = OPFEngine.ContentService.readVideoResourceByIdCultureAndVersion(videoResource.getId(), "RUSSIAN", 2);
        logger.info(response9.getMessage());
        Assert.assertFalse("VideoResource should not be retrieved.", response9.isSuccess());

        ServiceResponse response10 = OPFEngine.ContentService.deleteVideoResource(videoResource.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("VideoResource should be deleted.", response10.isSuccess());

        deleteFolder(folder);
    }

	@Test
	public void documentationTest() {
	    ResourceContent content = new ResourceContent();
	    content.setLookup(domain.getLookup());
	    content.setLookupSuffix("description");
	    content.setCulture(Cultures.AMERICAN);
	    content.setCountry("us");
	    content.setRegistryNodeId(domain.getId());
	    content.setResourceType(ResourceType.TEXT);
	    content.setResourceName("testResource");

	    ServiceResponse<ResourceContent> response = OPFEngine.ContentService.createDocumentation(content);
	    logger.info(response.getMessage());
	    Assert.assertTrue("Documentation should be created.", response.isSuccess());
	    content= response.getItem();
	    Assert.assertNotNull("Should not be null.", content);


	    content.setResourceName("testResource2");
	    ServiceResponse<ResourceContent> response5 = OPFEngine.ContentService.updateDocumentation(content.getResourceId(), content);
	    Assert.assertTrue("documentation should be updated.", response5.isSuccess());
	    List<ResourceContent> registryNodeTrees = response5.getData();
	    Assert.assertNotNull("Should not be null.", registryNodeTrees);
	    Assert.assertEquals("Should not be empty.", 1, registryNodeTrees.size());
	    ResourceContent updatedFolder = registryNodeTrees.get(0);
	    Assert.assertEquals("Should be the same name.", updatedFolder.getResourceName(), content.getResourceName());

//		Package aPackage = (Package) testContextAttributes.get(Elements.PACKAGE);
//		ServiceResponse response15 = OPFEngine.ContentService.getDocumentationHtmlSource(aPackage.getLookup(), "us", 1024, "");
//		logger.info(response15.getMessage());
//	    Assert.assertTrue(response15.getMessage(), response15.isSuccess());

	    ServiceResponse response6 = OPFEngine.ContentService.deleteFolder(content.getResourceId());
		logger.info(response6.getMessage());
	    Assert.assertTrue("documentation should be deleted.", response6.isSuccess());

		Entity entity = (Entity) testContextAttributes.get(Elements.ENTITY);

		ServiceResponse<AbstractResource> response9 = OPFEngine.ContentService.readDescriptionsByParentId(domain.getId(), "us");
		logger.info(response9.getMessage());
		Assert.assertTrue(response9.getMessage(), response9.isSuccess());
		ServiceResponse<FieldResource> response10 = OPFEngine.ContentService.readFields(entity.getId(),"us");
		logger.info(response10.getMessage());
		Assert.assertTrue(response10.getMessage(), response10.isSuccess());
		ServiceResponse<EntityResourceRelationship> response11 = OPFEngine.ContentService.readRelatedEntities(entity.getId(), "us");
		logger.info(response11.getMessage());
		Assert.assertTrue(response11.getMessage(), response11.isSuccess());
		ServiceResponse<ActionResource> response12 = OPFEngine.ContentService.readActions(entity.getId(), "us");
		logger.info(response12.getMessage());
		Assert.assertTrue(response12.getMessage(), response12.isSuccess());
		ServiceResponse<Lookup> response13 = OPFEngine.ContentService.readChildren(entity.getId());
		logger.info(response13.getMessage());
		Assert.assertTrue(response13.getMessage(), response13.isSuccess());
		ServiceResponse<ActionServiceLink> response7 = OPFEngine.ContentService.readEntityServiceLinks(response13.getItem().getId());
		logger.info(response7.getMessage());
		Assert.assertTrue(response7.getMessage(), response7.isSuccess());
		ServiceResponse<ActionServiceLink> response8 = OPFEngine.ContentService.readExamplesByParentId(response13.getItem().getId(),"us");
		logger.info(response8.getMessage());
		Assert.assertTrue(response8.getMessage(), response8.isSuccess());
		ServiceResponse<Lookup> response14 = OPFEngine.ContentService.readParents(entity.getId());
		logger.info(response14.getMessage());
		Assert.assertTrue(response14.getMessage(), response14.isSuccess());
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//    private Domain createDomain() {
//        Domain domain = new Domain();
//		domain.setName(time + "-test-domain");
//		domain.setParentId(1L);
//        ServiceResponse<RegistryNodeTree> response8 = OPFEngine.RegistryService.createDomain(domain);
//        logger.info(response8.getMessage());
//        Assert.assertTrue("Domain should be created.", response8.isSuccess());
//        domain.setId(response8.getItem().getId());
//        return domain;
//    }
//
//    private void deleteDomain(Domain domain) {
//        ServiceResponse response = OPFEngine.RegistryService.deleteDomain(domain.getId());
//        logger.info(response.getMessage());
//        Assert.assertTrue("Domain should be deleted.", response.isSuccess());
//    }

    private Folder createFolder(Long parentId) {
        Folder folder = new Folder();
        folder.setName(time + "-folder");
        folder.setParentId(parentId);
        folder.setType("FOLDER");
        return createFolder(folder);
    }

    private Folder createFolder(Folder folder) {
        ServiceResponse<RegistryNodeTree> response = OPFEngine.ContentService.createFolder(folder);
        logger.info(response.getMessage());
        Assert.assertTrue("Folder should be created.", response.isSuccess());

        RegistryNodeTree registryNodeTree = response.getItem();
        Assert.assertNotNull("Should not be null.", registryNodeTree);
        folder.setId(registryNodeTree.getId());
        folder.setLookup(registryNodeTree.getLookup());
        return folder;
    }

    private void deleteFolder(Folder folder) {
        ServiceResponse response = OPFEngine.ContentService.deleteFolder(folder.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("Folder should be deleted.", response.isSuccess());
    }

    private Collection createCollection(Folder folder) {
        Collection collection = new Collection();
        collection.setName(time + "-collection");
        collection.setParentId(folder.getId());
        collection.setType("COLLECTION");
        collection.setDescription("Some Description");
        return createCollection(collection);
    }

    private Collection createCollection(Collection collection) {
        ServiceResponse<RegistryNodeTree> response = OPFEngine.ContentService.createCollection(collection);
        logger.info(response.getMessage());
        Assert.assertTrue("Collection should be created.", response.isSuccess());
        RegistryNodeTree registryNodeTree = response.getItem();
        Assert.assertNotNull("Should not be null.", registryNodeTree);
        Assert.assertEquals("Should be the same name.", registryNodeTree.getName(), collection.getName());
        collection.setId(registryNodeTree.getId());
        return collection;
    }

    private void deleteCollection(Collection collection) {
        ServiceResponse<Collection> response = OPFEngine.ContentService.deleteCollection(collection.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("Collection should be deleted.", response.isSuccess());
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private <E1 extends Lookup, E2 extends Lookup> void assertExistElement(List<E1> elements, E2 element) {
        boolean exist = false;
        for (E1 elem : elements) {
            exist |= elem.getId().equals(element.getId());
        }
        Assert.assertTrue("Collection should contain in element.", exist);
    }

}
