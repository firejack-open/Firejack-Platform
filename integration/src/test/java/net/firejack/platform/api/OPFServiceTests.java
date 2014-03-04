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
