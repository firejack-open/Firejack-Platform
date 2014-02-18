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

package net.firejack.platform.api;

import net.firejack.platform.api.authority.AuthorityServiceTest;
import net.firejack.platform.api.authority.AuthorityServiceTest2;
import net.firejack.platform.api.config.ConfigServiceTest;
import net.firejack.platform.api.content.ContentServiceTest;
import net.firejack.platform.api.directory.DirectoryServiceTest;
import net.firejack.platform.api.filestore.FileStoreServiceTest;
import net.firejack.platform.api.process.*;
import net.firejack.platform.api.registry.RegistryServiceTest;
import net.firejack.platform.api.securitymanager.SecurityManagerServiceTest;
import net.firejack.platform.api.site.SiteServiceTest;
import net.firejack.platform.api.statistics.StatisticsAPITests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
        AuthorityServiceTest.class,
        AuthorityServiceTest2.class,
        StatisticsAPITests.class,
        ContentServiceTest.class,
        RegistryServiceTest.class,
        SecurityManagerServiceTest.class,
        SiteServiceTest.class,
        DirectoryServiceTest.class,
        ConfigServiceTest.class,
		ProcessActorServiceTest.class,
		ProcessCaseAttachmentServiceTest.class,
		ProcessCaseNoteServiceTest.class,
		ProcessCaseServiceTest.class,
		ProcessServiceTest.class,
		ProcessTaskServiceTest.class,
		FileStoreServiceTest.class
})
public class OPFServiceTests {
	public static  String ADMIN_LOGIN ="admin";
	public static  String ADMIN_PASSWORD ="111111";
}
