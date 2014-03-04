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

package net.firejack.platform.api.process;


import net.firejack.platform.api.Elements;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.api.TestBusinessContext;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.process.listener.ProcessExecutionListener;
import net.firejack.platform.api.registry.listener.PackageExecutionListener;
import net.firejack.platform.api.registry.listener.RootDomainExecutionListener;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.OpenFlame;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class,
        RootDomainExecutionListener.class,
        PackageExecutionListener.class,
        ProcessExecutionListener.class
})
public class ProcessCaseNoteServiceTest {
	protected static Logger logger = Logger.getLogger(ProcessCaseNoteServiceTest.class);

	@Value("${sts.base.url}")
	private String baseUrl;

    @Resource(name = "testContextAttributes")
    private Map<Elements, AbstractDTO> testContextAttributes;

	@Before
	public void setUp() {
    }

	@After
	public void tearDown() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

    private Long startCase(String entityType, Long entityId) {
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);
        String processLookup = process.getLookup();

        User user1 = (User) testContextAttributes.get(Elements.USER1);

        CaseOperationsParams startCaseParams = new CaseOperationsParams();
        startCaseParams.setAssigneeId(user1.getId());
        startCaseParams.setAllowNullAssignee(false);
        startCaseParams.setCaseDescription("test_case");
        startCaseParams.setNoteText("note");
        startCaseParams.setProcessLookup(processLookup);

        List<CaseObject> caseObjects = new ArrayList<CaseObject>();
        CaseObject caseObject = new CaseObject();
        caseObject.setEntityId(entityId);
        caseObject.setEntityType(entityType);
        caseObject.setProcessFields(Collections.<ProcessFieldCaseValue>emptyList());
        caseObjects.add(caseObject);
        startCaseParams.setCaseObjects(caseObjects);

        ServiceResponse<SimpleIdentifier<Long>> response = OPFEngine.ProcessService.startCase(startCaseParams);

        Assert.assertNotNull("Response shouldn't be null.", response);
        logger.info(response.getMessage());
        Long caseId = response.getItem().getIdentifier();
        Assert.assertNotNull("Case id shouldn't be null", caseId);

        return caseId;
    }

	@Test
	public void crud() {
        String noteText = "test note";

        Long caseId = startCase(OpenFlame.SYSTEM, 1234l);

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        User user1 = (User) testContextAttributes.get(Elements.USER1);

        CaseNote caseNote = new CaseNote();
        caseNote.setText(noteText);
        caseNote.setProcessCase(new Case());
        caseNote.getProcessCase().setId(caseId);
        caseNote.setUser(user1);

        ServiceResponse<CaseNote> createNoteResponse = OPFEngine.ProcessService.createCaseNote(caseNote);
        logger.info(createNoteResponse.getMessage());
        Long caseNoteId = createNoteResponse.getItem().getId();
        Assert.assertNotNull("Creted note should have an id", caseNoteId);

        ServiceResponse<CaseNote> readNoteResponse = OPFEngine.ProcessService.readCaseNote(caseNoteId);
        logger.info(readNoteResponse.getMessage());
        Assert.assertNotNull("Read note shouldn't be null", readNoteResponse.getItem());
        Assert.assertEquals("Read note should have the same text as created one", noteText, readNoteResponse.getItem().getText());

        CaseNote readCaseNote = readNoteResponse.getItem();
        String noteTextUpdate = "text updated";
        readCaseNote.setText(noteTextUpdate);
        ServiceResponse<CaseNote> updateNoteResponse = OPFEngine.ProcessService.updateCaseNote(caseNoteId, readCaseNote);
        logger.info(updateNoteResponse.getMessage());
        Assert.assertNotNull("Updated note shouldn't be null", updateNoteResponse.getItem());
        Assert.assertEquals("Updated note should have the same text as supplied", noteTextUpdate, updateNoteResponse.getItem().getText());

        ServiceResponse deleteNoteResponse = OPFEngine.ProcessService.deleteCaseNote(caseNoteId);
        logger.info(deleteNoteResponse.getMessage());
        Assert.assertTrue("Delete note should return success true", deleteNoteResponse.isSuccess());

        ServiceResponse<CaseNote> readNoteAfterDelResponse = OPFEngine.ProcessService.readCaseNote(caseNoteId);
        logger.info(readNoteAfterDelResponse.getMessage());
        Assert.assertNull("Read note should be null after deletion", readNoteAfterDelResponse.getItem());
    }

    @Test
    public void search() {
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);

        String noteText = "test note";
        long entityId = 1234l;

        Long caseId = startCase(OpenFlame.SYSTEM, entityId);

        User user1 = (User) testContextAttributes.get(Elements.USER1);

        CaseNote caseNote = new CaseNote();
        caseNote.setText(noteText);
        caseNote.setProcessCase(new Case());
        caseNote.getProcessCase().setId(caseId);
        caseNote.setUser(user1);

        ServiceResponse<CaseNote> createNoteResponse = OPFEngine.ProcessService.createCaseNote(caseNote);
        logger.info(createNoteResponse.getMessage());

        ServiceResponse<CaseNote> readByCaseResponse = OPFEngine.ProcessService.readCaseNotesByCase(caseId);
        logger.info(readByCaseResponse.getMessage());
        Assert.assertEquals("Read case notes count should be 1", new Integer(1), readByCaseResponse.getTotal());
        CaseNote firstCaseNote = readByCaseResponse.getData().iterator().next();
        Assert.assertEquals("Read case notes text should be equal to the created one", noteText, firstCaseNote.getText());

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        SearchTaskCaseFilter searchTaskCaseFilter = new SearchTaskCaseFilter();
        searchTaskCaseFilter.setProcessId(process.getId());
        searchTaskCaseFilter.setActive(true);
        ServiceResponse<Task> searchTasksResponse = OPFEngine.ProcessService.searchTasks(new ServiceRequest<SearchTaskCaseFilter>(searchTaskCaseFilter), null);
        logger.info(searchTasksResponse.getMessage());
        Task activeTask = searchTasksResponse.getData().iterator().next(); // should be only one
        
        ServiceResponse<CaseNote> readByTaskResponse = OPFEngine.ProcessService.readCaseNotesByTask(activeTask.getId());
        logger.info(readByTaskResponse.getMessage());
        Assert.assertEquals("Read case notes count should be 1", new Integer(1), readByTaskResponse.getTotal());
        firstCaseNote = readByTaskResponse.getData().iterator().next();
        Assert.assertEquals("Read case notes text should be equal to the created one", noteText, firstCaseNote.getText());

        ServiceResponse<CaseNote> readByCaseObjectResponse = OPFEngine.ProcessService.readCaseNotesByCaseObject(entityId, OpenFlame.SYSTEM);
        logger.info(readByCaseObjectResponse.getMessage());
        Assert.assertEquals("Read case notes count should be 1", new Integer(1), readByCaseObjectResponse.getTotal());
        firstCaseNote = readByCaseObjectResponse.getData().iterator().next();
        Assert.assertEquals("Read case notes text should be equal to the created one", noteText, firstCaseNote.getText());
    }

}