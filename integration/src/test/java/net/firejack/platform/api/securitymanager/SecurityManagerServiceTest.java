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

package net.firejack.platform.api.securitymanager;


import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.api.registry.listener.DomainExecutionListener;
import net.firejack.platform.api.registry.listener.EntityExecutionListener;
import net.firejack.platform.api.registry.listener.PackageExecutionListener;
import net.firejack.platform.api.registry.listener.RootDomainExecutionListener;
import net.firejack.platform.api.securitymanager.domain.SecuredRecord;
import net.firejack.platform.core.response.ServiceResponse;
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
public class SecurityManagerServiceTest {

    private static final Logger logger = Logger.getLogger(SecurityManagerServiceTest.class);

    private Long time;

    @Before
    public void setUp() {
        time = System.currentTimeMillis();
    }

    @After
    public void tearDown() {
        logger.info("Test execution time in milliseconds: " + (System.currentTimeMillis() - time));
    }

    @Test
    public void securedRecordTest() {
        ServiceResponse response = OPFEngine.SecurityManagerService.createSecuredRecord(
                1L, "testentity1", EntityExecutionListener.ENTITY_LOOKUP, null, null);
        Assert.assertTrue("Service Response should not be null", response != null);
        if (response.getMessage() != null) {
            logger.info("Service Response Message:" + response.getMessage());
        }
        Assert.assertTrue("Secured Record should be created.", response.isSuccess());

        //create child
        response = OPFEngine.SecurityManagerService.createSecuredRecord(
                2L, "testentity2", EntityExecutionListener.ENTITY_LOOKUP, 1L, EntityExecutionListener.ENTITY_LOOKUP);
        Assert.assertTrue("Service Response should not be null", response != null);
        if (response.getMessage() != null) {
            logger.info("Service Response Message:" + response.getMessage());
        }
        Assert.assertTrue("Secured Record should be created.", response.isSuccess());

        //read secured record
        ServiceResponse<SecuredRecord> securedRecordResponse =
                OPFEngine.SecurityManagerService.getSecuredRecordInfo(1L, EntityExecutionListener.ENTITY_LOOKUP);
        Assert.assertTrue("Service Response should not be null", securedRecordResponse != null);
        if (securedRecordResponse.getMessage() != null) {
            logger.info("Service Response Message:" + securedRecordResponse.getMessage());
        }
        Assert.assertTrue("Secured Record should be created.", securedRecordResponse.isSuccess());
        SecuredRecord securedRecord = securedRecordResponse.getItem();
        Assert.assertTrue("Returned secured record should not be null", securedRecord != null);
        Assert.assertTrue("Returned secured record should have id.", securedRecord.getId() != null);
        Assert.assertTrue("Returned secured record should have externalNumberId = 1",
                securedRecord.getExternalNumberId() == 1L);
        Assert.assertTrue("Returned secured record should have name = \"testentity1\"",
                "testentity1".equals(securedRecord.getName()));

        securedRecordResponse = OPFEngine.SecurityManagerService.getSecuredRecordInfo(
                2L, EntityExecutionListener.ENTITY_LOOKUP);
        Assert.assertTrue("Service Response should not be null", securedRecordResponse != null);
        if (securedRecordResponse.getMessage() != null) {
            logger.info("Service Response Message:" + securedRecordResponse.getMessage());
        }
        Assert.assertTrue("Secured Record should be created.", securedRecordResponse.isSuccess());
        securedRecord = securedRecordResponse.getItem();
        Assert.assertTrue("Returned secured record should not be null", securedRecord != null);
        Assert.assertTrue("Returned secured record should have id.", securedRecord.getId() != null);
        Assert.assertTrue("Returned secured record should have externalNumberId = 2",
                securedRecord.getExternalNumberId() == 2L);
        Assert.assertTrue("Returned secured record should have name = \"testentity2\"",
                "testentity2".equals(securedRecord.getName()));

        //update secured record
        response = OPFEngine.SecurityManagerService.updateSecuredRecord(
                securedRecord.getExternalNumberId(), EntityExecutionListener.ENTITY_LOOKUP, "testentity2 updated");
        Assert.assertTrue("Service Response should not be null", response != null);
        if (response.getMessage() != null) {
            logger.info("Service Response Message:" + response.getMessage());
        }
        Assert.assertTrue("Secured Record should be updated.", response.isSuccess());

        //read after update
        securedRecordResponse = OPFEngine.SecurityManagerService.getSecuredRecordInfo(
                2L, EntityExecutionListener.ENTITY_LOOKUP);
        Assert.assertTrue("Service Response should not be null", securedRecordResponse != null);
        if (securedRecordResponse.getMessage() != null) {
            logger.info("Service Response Message:" + securedRecordResponse.getMessage());
        }
        Assert.assertTrue("Secured Record should be created.", securedRecordResponse.isSuccess());
        securedRecord = securedRecordResponse.getItem();
        Assert.assertTrue("Returned secured record should not be null", securedRecord != null);
        Assert.assertTrue("Returned secured record should have id.", securedRecord.getId() != null);
        Assert.assertTrue("Returned secured record should have externalNumberId = 2",
                securedRecord.getExternalNumberId() == 2L);
        Assert.assertTrue("Returned secured record should have name = \"testentity2 updated\"",
                "testentity2 updated".equals(securedRecord.getName()));

        //delete secured record
        response = OPFEngine.SecurityManagerService.removeSecuredRecord(
                2L, EntityExecutionListener.ENTITY_LOOKUP);
        Assert.assertTrue("Service Response should not be null", response != null);
        if (response.getMessage() != null) {
            logger.info("Service Response Message:" + response.getMessage());
        }
        Assert.assertTrue("Secured Record should be removed.", response.isSuccess());

        response = OPFEngine.SecurityManagerService.removeSecuredRecord(
                1L, EntityExecutionListener.ENTITY_LOOKUP);
        Assert.assertTrue("Service Response should not be null", response != null);
        if (response.getMessage() != null) {
            logger.info("Service Response Message:" + response.getMessage());
        }
        Assert.assertTrue("Secured Record should be removed.", response.isSuccess());
    }

}