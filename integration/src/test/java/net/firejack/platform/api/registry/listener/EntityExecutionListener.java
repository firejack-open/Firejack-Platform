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

package net.firejack.platform.api.registry.listener;

import net.firejack.platform.api.Elements;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFServiceTests;
import net.firejack.platform.api.registry.domain.Domain;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.field.Field;
import net.firejack.platform.api.registry.model.EntityType;
import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.response.ServiceResponse;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.ArrayList;
import java.util.Map;

public class EntityExecutionListener extends AbstractTestExecutionListener {

    public static final String ENTITY_NAME = "Testentity";
    public static final String ENTITY_LOOKUP = "ru.testrootdomain.testpackage.testdomain.testentity";

    private static final Logger logger = Logger.getLogger(EntityExecutionListener.class);

    private Map<Elements, AbstractDTO> testContextAttributes;

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        testContextAttributes = (Map<Elements, AbstractDTO>)
                testContext.getApplicationContext().getBean("testContextAttributes");
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        Domain domain = (Domain) testContextAttributes.get(Elements.DOMAIN);

        Entity entity = new Entity();
		entity.setName(ENTITY_NAME);
		entity.setStatus(RegistryNodeStatus.UNKNOWN);
		entity.setTypeEntity(EntityType.STANDARD.getEntityType());
		entity.setParentId(domain.getId());

	    ArrayList<Field> fields = new ArrayList<Field>();
	    Field field = new Field();
	    field.setFieldType(FieldType.NAME);
	    field.setName("name");

	    fields.add(field);
	    entity.setFields(fields);

		ServiceResponse<RegistryNodeTree> response = OPFEngine.RegistryService.createEntity(entity);
		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create entity response null.", response);
		Assert.assertNotNull("Can't be create entity item null.", response.getItem());
		entity.setId(response.getItem().getId());

        testContextAttributes.put(Elements.ENTITY, entity);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
	    OPFEngine.init(OPFServiceTests.ADMIN_LOGIN, OPFServiceTests.ADMIN_PASSWORD);
        Domain domain = (Domain) testContextAttributes.get(Elements.DOMAIN);
        ServiceResponse response = OPFEngine.RegistryService.deleteDomain(domain.getId());
        logger.info(response.getMessage());
        Assert.assertTrue("Domain should be deleted.", response.isSuccess());
    }

}
